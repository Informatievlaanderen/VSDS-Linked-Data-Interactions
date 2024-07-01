package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import org.apache.jena.rdf.model.Model;

public class ComponentExecutorImpl implements ComponentExecutor {

	private final LdioTransformer ldiTransformerPipeline;

	public ComponentExecutorImpl(LdioTransformer ldiTransformerPipeline) {
		this.ldiTransformerPipeline = ldiTransformerPipeline;
	}

	@Override
	public void transformLinkedData(final Model linkedDataModel) {
		ldiTransformerPipeline.apply(linkedDataModel);
	}
}