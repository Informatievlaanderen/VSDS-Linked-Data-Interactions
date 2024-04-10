package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToOneTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.MemberInfo;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.*;

public class VersionObjectCreator implements LdiOneToOneTransformer {

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionObjectCreator.class);

	private static final Model initModel = ModelFactory.createDefaultModel();
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static final String XMLSCHEMA_DATE_TIME = "http://www.w3.org/2001/XMLSchema#dateTime";
	public static final Property SYNTAX_TYPE = initModel
			.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	public static final String DATE_OBSERVED_PROPERTY_COULD_NOT_BE_FOUND = "Date observed property could not be found: {}";
	public static final String MEMBER_INFO_COULD_NOT_BE_FOUND = "Member info could not be found";
	public static final String LINKED_DATA_MODEL_IS_EMPTY = "Received an empty data model";

	private final PropertyExtractor dateObservedPropertyExtractor;
	private final Resource memberTypeResource;
	private final String delimiter;
	private final Property generatedAtTimeProperty;
	private final Property versionOfProperty;

	public VersionObjectCreator(PropertyExtractor dateObservedPropertyExtractor, Resource memberTypeResource,
	                            String delimiter,
	                            Property generatedAtTimeProperty, Property versionOfProperty) {
		this.dateObservedPropertyExtractor = dateObservedPropertyExtractor;
		this.memberTypeResource = memberTypeResource;
		this.delimiter = delimiter;
		this.generatedAtTimeProperty = generatedAtTimeProperty;
		this.versionOfProperty = versionOfProperty;
	}

	@Override
	public Model transform(Model linkedDataModel) {
		final String dateObserved = getDateObserved(linkedDataModel);
		return extractMemberInfo(linkedDataModel)
				.map(memberInfo -> constructVersionObject(linkedDataModel, new MemberInfo(memberInfo, dateObserved)))
				.orElseGet(() -> {
					if (linkedDataModel.isEmpty()) {
						LOGGER.warn(LINKED_DATA_MODEL_IS_EMPTY);
					} else {
						LOGGER.warn(MEMBER_INFO_COULD_NOT_BE_FOUND);
					}

					return linkedDataModel;
				});
	}

	private Optional<String> extractMemberInfo(Model linkedDataModel) {
		return linkedDataModel.listStatements(null, SYNTAX_TYPE, memberTypeResource)
				.nextOptional()
				.map(Statement::getSubject)
				.map(Resource::asNode)
				.map(Node::toString);
	}

	private String getDateObserved(Model linkedDataModel) {
		return dateObservedPropertyExtractor
				.getProperties(linkedDataModel)
				.stream()
				.filter(RDFNode::isLiteral)
				.map(RDFNode::asLiteral)
				.filter(literal -> literal.getDatatype().getURI().toLowerCase().contains("datetime"))
				.map(Literal::getString)
				.findFirst()
				.orElseGet(() -> {
					String currentDate = LocalDateTime.now().format(formatter);
					LOGGER.warn(DATE_OBSERVED_PROPERTY_COULD_NOT_BE_FOUND, currentDate);
					return currentDate;
				});
	}

	protected Model constructVersionObject(Model inputModel, MemberInfo memberInfo) {
		Model versionObjectModel = ModelFactory.createDefaultModel();
		String versionObjectId = memberInfo.generateVersionObjectId(delimiter);
		Resource subject = versionObjectModel.createResource(versionObjectId);

		LOGGER.info("Created version: {}", versionObjectId);

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
