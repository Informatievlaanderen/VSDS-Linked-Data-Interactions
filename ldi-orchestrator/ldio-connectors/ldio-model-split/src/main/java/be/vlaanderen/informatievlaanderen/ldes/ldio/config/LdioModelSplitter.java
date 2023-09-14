package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioProcessor;
import org.apache.jena.rdf.model.Model;

public class LdioModelSplitter extends LdioProcessor {
	private final ModelSplitTransformer modelSplitter;

	protected LdioModelSplitter(String subjectType) {
		this.modelSplitter = new ModelSplitTransformer(subjectType);
	}

	@Override
	public void apply(Model model) {
		modelSplitter.transform(model).forEach(this::next);
	}
}
