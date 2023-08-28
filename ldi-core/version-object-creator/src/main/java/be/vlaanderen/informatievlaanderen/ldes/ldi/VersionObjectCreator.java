package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.MemberInfo;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.*;

public class VersionObjectCreator implements LdiTransformer {

	private static final Model initModel = ModelFactory.createDefaultModel();
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static final String XMLSCHEMA_DATE_TIME = "http://www.w3.org/2001/XMLSchema#dateTime";
	public static final Property SYNTAX_TYPE = initModel.createProperty(
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

	private final Property dateObservedProperty;
	private final Resource memberTypeResource;
	private final String delimiter;
	private final Property generatedAtTimeProperty;
	private final Property versionOfProperty;

	public VersionObjectCreator(Property dateObservedProperty, Resource memberTypeResource, String delimiter,
			Property generatedAtTimeProperty, Property versionOfProperty) {
		this.dateObservedProperty = dateObservedProperty;
		this.memberTypeResource = memberTypeResource;
		this.delimiter = delimiter;
		this.generatedAtTimeProperty = generatedAtTimeProperty;
		this.versionOfProperty = versionOfProperty;
	}

	@Override
	public Model apply(Model linkedDataModel) {
		MemberInfo memberInfo = extractMemberInfo(linkedDataModel, memberTypeResource, dateObservedProperty);

		return constructVersionObject(linkedDataModel, memberInfo);
	}

	private MemberInfo extractMemberInfo(Model linkedDataModel, Resource memberTypeResource,
			Property dateObservedProperty) {
		String dateObserved = Optional.ofNullable(dateObservedProperty)
				.map(property -> linkedDataModel.listStatements(null, property, (Resource) null)
						.mapWith(Statement::getObject)
						.filterKeep(RDFNode::isLiteral)
						.mapWith(RDFNode::asLiteral)
						.filterKeep(literal -> literal.getDatatype().getURI().contains("DateTime"))
						.nextOptional()
						.map(Literal::getString)
						.orElse(LocalDateTime.now().format(formatter)))
				.orElse(LocalDateTime.now().format(formatter));
		String id = linkedDataModel.listStatements(null, SYNTAX_TYPE, memberTypeResource)
				.nextOptional()
				.map(Statement::getSubject)
				.map(Resource::asNode)
				.map(Node::toString)
				.orElseThrow();
		return new MemberInfo(id, dateObserved);
	}

	protected Model constructVersionObject(Model inputModel, MemberInfo memberInfo) {
		Model versionObjectModel = ModelFactory.createDefaultModel();
		String versionObjectId = memberInfo.generateVersionObjectId(delimiter);
		Resource subject = versionObjectModel.createResource(versionObjectId);

		List<Statement> statementList = inputModel.listStatements().toList();
		statementList.stream()
				.filter(stmt -> stmt.getSubject().equals(inputModel.createResource(memberInfo.getVersionOf())))
				.forEach(stmt -> versionObjectModel.add(subject, stmt.getPredicate(), stmt.getObject()));
		statementList.stream()
				.filter(stmt -> !stmt.getSubject().equals(inputModel.createResource(memberInfo.getVersionOf())))
				.forEach(versionObjectModel::add);

		addOptionalProperties(versionObjectModel, subject, memberInfo);

		return versionObjectModel;
	}

	private void addOptionalProperties(Model versionObjectModel, Resource subject, MemberInfo memberInfo) {
		if (null != generatedAtTimeProperty) {
			versionObjectModel.add(createStatement(subject, generatedAtTimeProperty,
					createTypedLiteral(memberInfo.getObservedAt(),
							TypeMapper.getInstance().getTypeByName(XMLSCHEMA_DATE_TIME))));
		}

		if (null != versionOfProperty) {
			versionObjectModel.add(
					createStatement(subject, versionOfProperty, createResource(memberInfo.getVersionOf())));
		}
	}
}
