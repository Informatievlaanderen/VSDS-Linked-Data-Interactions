package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.NgsiV2ToLdAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.NgsiV2ToLdProcessorProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.jena.riot.Lang;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.NgsiV2ToLdProcessorProperties.CORE_CONTEXT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.NgsiV2ToLdProcessorProperties.DATA_IDENTIFIER;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.NgsiV2ToLdProcessorProperties.LD_CONTEXT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ngsiv2-to-ld", "ldes", "vsds" })
@CapabilityDescription("Translate and transform NGSIv2 data to NGSI-LD")
public class NgsiV2ToLdProcessor extends AbstractProcessor {
	protected NgsiV2ToLdAdapter ngsiV2ToLdAdapter;

	protected String dataIdentifier;
	protected String coreContext;
	protected String ldContext;

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(SUCCESS, FAILURE);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(DATA_IDENTIFIER, CORE_CONTEXT, LD_CONTEXT);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		dataIdentifier = NgsiV2ToLdProcessorProperties.getDataIdentifier(context);
		coreContext = NgsiV2ToLdProcessorProperties.getCoreContext(context);
		ldContext = NgsiV2ToLdProcessorProperties.getLdContext(context);

		ngsiV2ToLdAdapter = new NgsiV2ToLdAdapter(dataIdentifier, coreContext, ldContext);
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		FlowFile flowFile = session.get();
		String content = FlowManager.receiveData(session, flowFile);
		String mimeType = flowFile.getAttribute("mime.type");

		try {
			ngsiV2ToLdAdapter.apply(LdiAdapter.Content.of(content, mimeType))
					.forEach(model -> FlowManager.sendRDFToRelation(session, flowFile, model, SUCCESS, Lang.JSONLD));
		} catch (Exception e) {
			FlowManager.sendRDFToRelation(session, flowFile, content, FAILURE, Lang.JSONLD);
		}
	}

}
