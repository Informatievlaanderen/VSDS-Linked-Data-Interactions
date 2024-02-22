package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioRepositoryMaterialiserProperties;
import org.apache.jena.rdf.model.Model;

public class LdioRepositoryMaterialiser implements LdiOutput {
	public static final String NAME = "Ldio:RepositoryMaterialiser";
	private final Materialiser materialiser;

	public LdioRepositoryMaterialiser(LdioRepositoryMaterialiserProperties properties) {
		this.materialiser = new Materialiser(
				properties.getSparqlHost(),
				properties.getRepositoryId(),
				properties.getNamedGraph(),
				properties.getBatchSize(),
				properties.getBatchTimeout()
		);
	}

	@Override
	public void accept(Model model) {
		materialiser.process(model);
	}
}
