package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.ObjectIdentifierException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.StatementImpl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class VersionMaterialiser implements LdiTransformer {
	private final Property versionPredicate;
	private final boolean restrictToMembers;

	public VersionMaterialiser(Property versionPredicate, boolean restrictToMembers) {
		this.versionPredicate = versionPredicate;
		this.restrictToMembers = restrictToMembers;
	}

	@Override
	public Model transform(Model linkedDataModel) {
		Model versionMaterialisedModel = ModelFactory.createDefaultModel();

		Map<Resource, Resource> versionIDEntityIDMap = getVersionIDEntityIDMap(linkedDataModel, versionPredicate);

		linkedDataModel.listStatements().forEach(statement -> {
			Resource subject = statement.getSubject();
			Property predicate = statement.getPredicate();
			RDFNode object = statement.getObject();

			// Statement needs 'de-versioning', replacing the subject.
			if (versionIDEntityIDMap.containsKey(statement.getSubject()))
				subject = versionIDEntityIDMap.get(statement.getSubject());

			// Object references a versioned entity, replace it with the 'de-versioned'
			// identifier.
			if (statement.getObject().isResource()
					&& versionIDEntityIDMap.containsKey(statement.getObject()))
				object = versionIDEntityIDMap.get(statement.getObject());

			// Don't add isVersionOf statements.
			if (statement.getPredicate().equals(versionPredicate))
				return;

			versionMaterialisedModel.add(new StatementImpl(subject, predicate, object));
		});
		Model finalVersionMaterialisedModel = versionMaterialisedModel;
		if (restrictToMembers) {
			finalVersionMaterialisedModel = reduceToLDESMemberOnlyModel(finalVersionMaterialisedModel);
		}
		return finalVersionMaterialisedModel;
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
	 * @param inputModel The model to reduce.
	 * @return The reduced model.
	 */
	private Model reduceToLDESMemberOnlyModel(Model inputModel) {
		Deque<Resource> subjectsOfIncludedStatements = new ArrayDeque<>();
		Model ldesMemberModel = ModelFactory.createDefaultModel();

		// LDES Member statements
		inputModel.listStatements(null, new PropertyImpl(Tree.MEMBER.toString()), (RDFNode) null)
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
