package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.ObjectIdentifierException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.StatementImpl;

import java.util.*;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class VersionMaterialiser implements LdiTransformer {
	private final Property versionPredicate;
	private final boolean restrictToMembers;

	public VersionMaterialiser(Property versionPredicate, boolean restrictToMembers) {
		this.versionPredicate = versionPredicate;
		this.restrictToMembers = restrictToMembers;
	}

	@Override
	public Model apply(Model linkedDataModel) {
		final Model versionMaterialisedModel = ModelFactory.createDefaultModel();

		Map<Resource, Resource> versionIDEntityIDMap = getVersionIDEntityIDMap(linkedDataModel, versionPredicate);

		List<Statement> deversionedStatements = linkedDataModel.listStatements()
				.toList().stream()
				.filter(statement -> !statement.getPredicate().equals(versionPredicate)) // drop isVersionOf stmts
				.map(statement -> deversionStatement(versionIDEntityIDMap, statement))
				.toList();

		versionMaterialisedModel.add(deversionedStatements);

		if (restrictToMembers) {
			return reduceToLDESMemberOnlyModel(versionMaterialisedModel);
		}
		return versionMaterialisedModel;
	}

	private Statement deversionStatement(Map<Resource, Resource> versionIdEntityIdMap, Statement statement) {
		Resource subject = statement.getSubject();
		RDFNode object = statement.getObject();

		// Statement needs 'de-versioning', replacing the subject.
		if (versionIdEntityIdMap.containsKey(subject))
			subject = versionIdEntityIdMap.get(subject);

		// Object references a versioned entity, replace it with the 'de-versioned'
		// identifier.
		if (statement.getObject().isResource()
				&& versionIdEntityIdMap.containsKey(object))
			object = versionIdEntityIdMap.get(object);

		return new StatementImpl(subject, statement.getPredicate(), object);
	}

	private static Map<Resource, Resource> getVersionIDEntityIDMap(Model model, Property isVersionOfPredicate) {
		Map<Resource, Resource> map = new HashMap<>();
		model.listStatements(null, isVersionOfPredicate, (RDFNode) null).forEach(memberStatement -> {
			if (!memberStatement.getObject().isResource()) {
				throw new ObjectIdentifierException(memberStatement);
			}
			map.put(memberStatement.getSubject(), memberStatement.getObject().asResource());
		});
		return map;
	}

	/**
	 * Builds a model limited to statements about the ldes:member, including
	 * potential nested blank nodes.
	 * Excludes statements about referenced entities, provided as context.
	 *
	 * @param inputModel
	 *            The model to reduce.
	 * @return The reduced model.
	 */
	private Model reduceToLDESMemberOnlyModel(Model inputModel) {
		Deque<Resource> subjectsOfIncludedStatements = new ArrayDeque<>();
		Model ldesMemberModel = ModelFactory.createDefaultModel();

		// LDES Member statements
		inputModel.listStatements(null, createProperty(Tree.MEMBER.toString()), (RDFNode) null)
				.forEach(memberStatement -> subjectsOfIncludedStatements.push((Resource) memberStatement.getObject()));

		/*
		 * LDES members can contain blank node references. All statements of those blank
		 * nodes
		 * need to be included in the output as well. Blank nodes can be nested,
		 * hence the need to keep track of them with a stack.
		 */
		while (!subjectsOfIncludedStatements.isEmpty()) {
			Resource subject = subjectsOfIncludedStatements.pop();
			inputModel.listStatements(subject, null, (RDFNode) null).forEach((Statement includedStatement) -> {
				ldesMemberModel.add(includedStatement);
				RDFNode object = includedStatement.getObject();
				if (object.isAnon()) {
					subjectsOfIncludedStatements.push(object.asResource());
				}
			});
		}
		return ldesMemberModel;
	}
}
