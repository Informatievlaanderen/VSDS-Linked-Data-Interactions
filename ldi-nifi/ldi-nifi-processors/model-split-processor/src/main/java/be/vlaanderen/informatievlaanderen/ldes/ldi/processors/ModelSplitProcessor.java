package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ModelSplitProperties;
import org.apache.jena.rdf.model.Model;
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

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ModelSplitProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.*;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ldes, vsds, split" })
@CapabilityDescription("Splits a single model into multiple models.")
public class ModelSplitProcessor extends AbstractProcessor {

	private ModelSplitter modelSplitter;

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
		modelSplitter = new ModelSplitter();
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) {
		final FlowFile flowFile = session.get();
		if (flowFile != null) {
			try {
				Lang dataSourceFormat = ModelSplitProperties.getDataSourceFormat(context);
				Model inputModel = receiveDataAsModel(session, flowFile, dataSourceFormat);
				modelSplitter
						.split(inputModel, ModelSplitProperties.getSubjectType(context))
						.forEach(model -> sendRDFToRelation(session, session.create(), model, SUCCESS,
								dataSourceFormat));
				sendRDFToRelation(session, flowFile, PROCESSED_INPUT_FILE);
			} catch (Exception e) {
				getLogger().error("Error splitting model in multiple models: {}", e.getMessage());
				sendRDFToRelation(session, flowFile, FAILURE);
			}
		}
	}

}
