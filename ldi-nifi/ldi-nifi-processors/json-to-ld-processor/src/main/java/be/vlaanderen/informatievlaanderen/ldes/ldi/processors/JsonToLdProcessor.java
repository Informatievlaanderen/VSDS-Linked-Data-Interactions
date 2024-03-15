package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.JsonToLdAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.JsonToLdProcessorProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.parser.JenaContextProvider;
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

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.JsonToLdProcessorProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "json-to-ld", "ldes", "vsds" })
@CapabilityDescription("Add ld-context to JSON")
public class JsonToLdProcessor extends AbstractProcessor {
	protected JsonToLdAdapter adapter;

	protected String coreContext;

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(SUCCESS, FAILURE);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(CONTEXT, FORCE_CONTENT_TYPE);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		coreContext = JsonToLdProcessorProperties.getCoreContext(context);
		boolean forceContentType = getForceContentType(context);

		adapter = new JsonToLdAdapter(coreContext, forceContentType, JenaContextProvider.create().getContext());
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		FlowFile flowFile = session.get();
		String content = FlowManager.receiveData(session, flowFile);
		String mimeType = flowFile.getAttribute("mime.type");

		try {
			adapter.apply(LdiAdapter.Content.of(content, mimeType))
					.forEach(model -> FlowManager.sendRDFToRelation(session, flowFile, model, SUCCESS, Lang.JSONLD));
		} catch (Exception e) {
			getLogger().error("Error transforming input to json-ld: {}", e.getMessage());
			FlowManager.sendRDFToRelation(session, flowFile, content, FAILURE, Lang.JSONLD);
		}
	}

}
