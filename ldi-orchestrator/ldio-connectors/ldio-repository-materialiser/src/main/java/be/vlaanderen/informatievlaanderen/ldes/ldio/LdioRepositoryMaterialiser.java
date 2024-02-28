package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioRepositoryMaterialiserProperties;
import org.apache.jena.rdf.model.Model;

public class LdioRepositoryMaterialiser implements LdiOutput {
	public static final String NAME = "Ldio:RepositoryMaterialiser";
	private final LdioMaterialiserRepositoryBatchCollector batchCollector;

	public LdioRepositoryMaterialiser(LdioRepositoryMaterialiserProperties properties) {
		final Materialiser materialiser = new Materialiser(
				properties.getSparqlHost(),
				properties.getRepositoryId(),
				properties.getNamedGraph()
		);
		this.batchCollector = new LdioMaterialiserRepositoryBatchCollector(
				properties.getBatchSize(),
				properties.getBatchTimeout(),
				materialiser
		);
	}

	@Override
	public void accept(Model model) {
		batchCollector.addMemberToCommit(model);
	}
}
