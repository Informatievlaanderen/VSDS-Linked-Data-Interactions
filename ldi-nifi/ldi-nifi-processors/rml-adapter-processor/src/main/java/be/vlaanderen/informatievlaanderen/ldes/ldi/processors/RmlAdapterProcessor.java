package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.RmlAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.jena.riot.RDFWriter;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RmlAdapterProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({"ldes", "vsds", "rml"})
@CapabilityDescription("Converts a non-LD object to an RDF object")
public class RmlAdapterProcessor extends AbstractProcessor {
	private RmlAdapter adapter;

	@Override
	protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(RML_MAPPING_CONTENT, RML_MAPPING_FILE, DATA_DESTINATION_FORMAT);
	}

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(SUCCESS, FAILURE);
	}

	@Override
	protected Collection<ValidationResult> customValidate(ValidationContext validationContext) {
		final boolean isRmlMappingContentSet = validationContext.getProperty(RML_MAPPING_CONTENT).isSet();
		final boolean isRmlMappingFileSet = validationContext.getProperty(RML_MAPPING_FILE).isSet();

		if (isRmlMappingContentSet && isRmlMappingFileSet) {
			return List.of(
					new ValidationResult.Builder()
							.subject("RML mapping")
							.valid(false)
							.explanation("both RML mapping content and RML mapping file cannot be set at the same time")
							.build()
			);
		}

		if (!isRmlMappingContentSet && !isRmlMappingFileSet) {
			return List.of(
					new ValidationResult.Builder()
							.subject("RML mapping")
							.valid(false)
							.explanation("either RML mapping content or RML mapping file must be set")
							.build()
			);
		}

		return List.of(new ValidationResult.Builder().valid(true).build());
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		final String mappingString = getRmlMapping(context);
		adapter = new RmlAdapter(mappingString);
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		final FlowFile flowFile = session.get();

		if (flowFile == null) {
			return;
		}

		final String content = FlowManager.receiveData(session, flowFile);
		final String mimeType = flowFile.getAttribute("mime.type");

		try {
			adapter.apply(LdiAdapter.Content.of(content, mimeType))
					.map(model -> RDFWriter.source(model).lang(getDataDestinationFormat(context)).asString())
					.forEach(data -> FlowManager.sendRDFToRelation(session, data, SUCCESS, getDataDestinationFormat(context)));
			session.remove(flowFile);
		} catch (Exception e) {
			getLogger().error("Error transforming input to an RDF object: {}", e.getMessage());
			session.transfer(flowFile, FAILURE);
		}

	}
}
