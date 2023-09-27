package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.rdf.model.Model;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioRepositoryMaterialiserProperties.*;

public class LdioRepositoryMaterialiser implements LdiOutput {

	private Materialiser materialiser;

	public LdioRepositoryMaterialiser(ComponentProperties config) {
		this.materialiser = new Materialiser(config.getProperty(SPARQL_HOST), config.getProperty(REPOSITORY_ID),
				config.getOptionalProperty(NAMED_GRAPH).orElse(""));
	}

	@Override
	public void accept(Model model) {
		materialiser.process(model);
	}
}
