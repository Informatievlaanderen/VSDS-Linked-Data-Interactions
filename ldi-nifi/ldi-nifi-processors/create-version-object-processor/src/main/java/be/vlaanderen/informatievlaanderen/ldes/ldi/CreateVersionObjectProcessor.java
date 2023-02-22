package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiLdToLdesMemberProcessorPropertyDescriptors.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiLdToLdesMemberProcessorRelationships.DATA_RELATIONSHIP;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiLdToLdesMemberProcessorRelationships.DATA_UNPARSEABLE_RELATIONSHIP;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ngsild", "ldes", "vsds" })
@CapabilityDescription("Converts NGSI-LD to LdesMembers and send them to the next processor")
public class CreateVersionObjectProcessor extends AbstractProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateVersionObjectProcessor.class);
	private List<PropertyDescriptor> descriptors;
	private Set<Relationship> relationships;
	private VersionObjectCreator versionObjectCreator;
	private Lang dataDestinationFormat;

	@Override
	protected void init(final ProcessorInitializationContext context) {
		descriptors = new ArrayList<>();
		descriptors.add(MEMBER_RDF_SYNTAX_TYPE);
		descriptors.add(DELIMITER);
		descriptors.add(DATE_OBSERVED_VALUE_JSON_PATH);
		descriptors.add(VERSION_OF_KEY);
		descriptors.add(DATA_DESTINATION_FORMAT);
		descriptors.add(GENERATED_AT_TIME_PROPERTY);
		descriptors = Collections.unmodifiableList(descriptors);

		relationships = new HashSet<>();
		relationships.add(DATA_RELATIONSHIP);
		relationships.add(DATA_UNPARSEABLE_RELATIONSHIP);
		relationships = Collections.unmodifiableSet(relationships);
	}

	@Override
	public Set<Relationship> getRelationships() {
		return this.relationships;
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return descriptors;
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		Model initModel = ModelFactory.createDefaultModel();

		Property dateObservedProperty = getDateObservedValueJsonPath(context);
		Resource memberType = getMemberRdfSyntaxType(context);
		String delimiter = getDelimiter(context);
		Property versionOfKey = getVersionOfKey(context);
		Property generatedAtTimeProperty = getGeneratedAtTimeProperty(context);
		dataDestinationFormat = getDataDestinationFormat(context);

		versionObjectCreator = new VersionObjectCreator(dateObservedProperty, memberType, delimiter, generatedAtTimeProperty,
				versionOfKey);
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) {
		LOGGER.info("On Trigger");
		FlowFile flowFile = session.get();

		String content = FlowManager.receiveData(session, flowFile);
		try {
			Model input = RDFParserBuilder.create().fromString(content).lang(Lang.JSONLD).toModel();
			Model versionObject = versionObjectCreator.transform(input);

			FlowManager.sendRDFToRelation(session, flowFile, versionObject, DATA_RELATIONSHIP, dataDestinationFormat);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			FlowManager.sendRDFToRelation(session, flowFile, content, DATA_UNPARSEABLE_RELATIONSHIP, Lang.JSONLD);
		}
	}

}
