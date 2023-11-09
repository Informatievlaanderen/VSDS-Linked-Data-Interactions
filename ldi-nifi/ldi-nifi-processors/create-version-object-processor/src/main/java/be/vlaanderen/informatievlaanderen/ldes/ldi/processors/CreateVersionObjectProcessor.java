package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.EmptyPropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import org.apache.jena.rdf.model.Model;
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

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CreateVersionObjectProcessorPropertyDescriptors.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CreateVersionObjectProcessorRelationships.*;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ldes", "vsds" })
@CapabilityDescription("Converts state objects to version objects")
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
		descriptors.add(DATE_OBSERVED_VALUE_RDF_PROPERTY);
		descriptors.add(VERSION_OF_KEY);
		descriptors.add(DATA_DESTINATION_FORMAT);
		descriptors.add(DATA_INPUT_FORMAT);
		descriptors.add(GENERATED_AT_TIME_PROPERTY);
		descriptors = Collections.unmodifiableList(descriptors);

		relationships = new HashSet<>();
		relationships.add(DATA_RELATIONSHIP);
		relationships.add(DATA_UNPARSEABLE_RELATIONSHIP);
		relationships.add(VALUE_NOT_FOUND_RELATIONSHIP);
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
		String dateObservedProperty = getDateObservedValue(context);
		createProperty(dateObservedProperty);
		PropertyExtractor dateObservedPropertyExtractor = dateObservedProperty != null
				? PropertyPathExtractor.from(dateObservedProperty)
				: new EmptyPropertyExtractor();
		Resource memberType = getMemberRdfSyntaxType(context);
		String delimiter = getDelimiter(context);
		Property versionOfKey = getVersionOfKey(context);
		Property generatedAtTimeProperty = getGeneratedAtTimeProperty(context);
		dataDestinationFormat = getDataDestinationFormat(context);

		versionObjectCreator = new VersionObjectCreator(dateObservedPropertyExtractor, memberType, delimiter,
				generatedAtTimeProperty,
				versionOfKey);
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) {
		LOGGER.info("On Trigger");
		FlowFile flowFile = session.get();
		Lang lang = nameToLang(context.getProperty(DATA_INPUT_FORMAT).getValue());

		String content = FlowManager.receiveData(session, flowFile);
		try {
			Model input = RDFParserBuilder.create().fromString(content).lang(lang).toModel();
			Model versionObject = versionObjectCreator.transform(input);

			if (versionObject.isIsomorphicWith(input)) {
				FlowManager.sendRDFToRelation(session, flowFile, versionObject,
						VALUE_NOT_FOUND_RELATIONSHIP, dataDestinationFormat);
			} else {
				FlowManager.sendRDFToRelation(session, flowFile, versionObject,
						DATA_RELATIONSHIP, dataDestinationFormat);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			FlowManager.sendRDFToRelation(session, flowFile, content, DATA_UNPARSEABLE_RELATIONSHIP, Lang.JSONLD);
		}
	}

}
