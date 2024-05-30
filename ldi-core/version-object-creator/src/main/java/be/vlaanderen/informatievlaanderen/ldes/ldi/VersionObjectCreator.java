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

/**
 * Will transform a State Object into a Version Object.
 */
public class VersionObjectCreator implements LdiOneToOneTransformer {

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionObjectCreator.class);

	private static final Model initModel = ModelFactory.createDefaultModel();
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static final String XMLSCHEMA_DATE_TIME = "http://www.w3.org/2001/XMLSchema#dateTime";
	public static final Property SYNTAX_TYPE = initModel
			.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

	public static final String DATE_OBSERVED_PROPERTY_COULD_NOT_BE_FOUND = "Date observed property could not be found, taking current datetime: {}";
	public static final String STATEMENT_NOT_FOUND = "Statement could not be found: {}";
	public static final String LINKED_DATA_MODEL_IS_EMPTY = "Received an empty data model";
	public static final String CREATED_VERSION = "Created version: {}";

	/**
	 * Extractor that will be used to generate a timestamp for the version object
	 */
	private final PropertyExtractor dateObservedPropertyExtractor;
	/**
	 * Representation of the member type resource that the state object represents
	 */
	private final Resource memberTypeResource;
	/**
	 * Represents that needs to be used to expand the named subject of the member to a version object member id
	 */
	private final String delimiter;
	/**
	 * Property path that will be used as a predicate to foresee the state object of a timestamp
	 */
	private final Property generatedAtTimeProperty;
	/**
	 * Property path that will be used a predicate to foresee the state object of a version-of
	 */
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
		Optional<String> memberInfo = extractMemberInfo(linkedDataModel);

		if (memberInfo.isPresent())
			return constructVersionObject(linkedDataModel, new MemberInfo(memberInfo.get(), dateObserved));

		if (linkedDataModel.isEmpty()) {
			LOGGER.warn(LINKED_DATA_MODEL_IS_EMPTY);
		} else {
			LOGGER.warn(STATEMENT_NOT_FOUND, SYNTAX_TYPE.getNameSpace());
		}

		return linkedDataModel;
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

		LOGGER.info(CREATED_VERSION, versionObjectId);

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
