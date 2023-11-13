package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ModelSplitProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ModelSplitProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.*;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ldes, vsds, split" })
@CapabilityDescription("Splits a single model into multiple models.")
public class ModelSplitProcessor extends AbstractProcessor {

	private ModelSplitTransformer modelSplitter;

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(SUCCESS, FAILURE, PROCESSED_INPUT_FILE);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(DATA_SOURCE_FORMAT, SUBJECT_TYPE);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		modelSplitter = new ModelSplitTransformer(ModelSplitProperties.getSubjectType(context));
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) {
		final FlowFile flowFile = session.get();
		if (flowFile != null) {
			try {
				Lang dataSourceFormat = determineDataSourceFormat(flowFile, context);
				Model inputModel = receiveDataAsModel(session, flowFile, dataSourceFormat);
				modelSplitter.transform(inputModel)
						.forEach(model -> sendRDFToRelation(session, session.create(), model, SUCCESS,
								dataSourceFormat));
				sendRDFToRelation(session, flowFile, inputModel, PROCESSED_INPUT_FILE, dataSourceFormat);
			} catch (Exception e) {
				getLogger().error("Error splitting model in multiple models: {}", e.getMessage());
				sendRDFToRelation(session, flowFile, FAILURE);
			}
		}
	}

	private Lang determineDataSourceFormat(FlowFile flowFile, ProcessContext context) {
		// Optional config can overwrite the mime types of the flowfiles
		final Lang configDataSourceFormat = ModelSplitProperties.getDataSourceFormat(context);
		if (configDataSourceFormat != null) {
			return configDataSourceFormat;
		}

		return RDFLanguages.contentTypeToLang(flowFile.getAttribute(CoreAttributes.MIME_TYPE.key()));
	}

}
