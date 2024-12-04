package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SkolemisationTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
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

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SkolemisationTransformerProperties.SKOLEM_DOMAIN;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SkolemisationTransformerProperties.getSkolemDomain;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.*;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({"skolemisation", "vsds", "ldes"})
@CapabilityDescription("Transforms blank nodes from LDES members to a skolemized URI")
public class SkolemisationTransformerProcessor extends AbstractProcessor {
	private SkolemisationTransformer skolemisationTransformer;

	@Override
	protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(SKOLEM_DOMAIN);
	}

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(SUCCESS, FAILURE);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		skolemisationTransformer = new SkolemisationTransformer(getSkolemDomain(context));
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		final FlowFile flowFile = session.get();
		if(flowFile != null) {
			try {
				final Lang mimeType = RDFLanguages.contentTypeToLang(flowFile.getAttribute("mime.type"));
				final Model inputModel = receiveDataAsModel(session, flowFile, mimeType);

				final Model result = skolemisationTransformer.transform(inputModel);
				sendRDFToRelation(session, flowFile, result, SUCCESS, mimeType);
			} catch (Exception e) {
				getLogger().error("Error executing skolemisation transformation: {}", e.getMessage());
				session.transfer(flowFile, FAILURE);
			}
		}
	}
}
