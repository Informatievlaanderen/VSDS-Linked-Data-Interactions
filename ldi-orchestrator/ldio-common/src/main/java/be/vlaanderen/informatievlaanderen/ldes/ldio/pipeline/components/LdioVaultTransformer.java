package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components;

import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * This LdioTransformer can be used for test purposes.
 */
public class LdioVaultTransformer extends LdioTransformer {
	private final List<Model> models;

	public LdioVaultTransformer() {
		this.models = new ArrayList<>();
	}

	@Override
	public void apply(Model model) {
		models.add(model);
	}

	public List<Model> getModels() {
		return models;
	}
}
