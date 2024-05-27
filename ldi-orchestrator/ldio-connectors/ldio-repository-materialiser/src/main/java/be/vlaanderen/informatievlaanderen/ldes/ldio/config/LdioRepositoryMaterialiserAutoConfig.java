package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRepositoryMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRepositoryMaterialiser.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioRepositoryMaterialiserProperties.*;

@Configuration
public class LdioRepositoryMaterialiserAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public LdioOutputConfigurator ldiRepoMaterialiserConfigurator() {
		return new LdiRepoMaterialiserProcessorConfigurator();
	}

	public static class LdiRepoMaterialiserProcessorConfigurator implements LdioOutputConfigurator {

		@Override
		public LdiComponent configure(ComponentProperties config) {
			final String hostUrl = config.getProperty(SPARQL_HOST);
			final String repositoryId = config.getProperty(REPOSITORY_ID);
			final String namedGraph = config.getOptionalProperty(NAMED_GRAPH).orElse("");
			final Materialiser materialiser = new Materialiser(hostUrl, repositoryId, namedGraph);
			final int batchSize = config.getOptionalInteger(BATCH_SIZE).orElse(BATCH_SIZE_DEFAULT);
			final int batchTimeout = config.getOptionalInteger(BATCH_TIMEOUT).orElse(BATCH_TIMEOUT_DEFAULT);
			return new LdioRepositoryMaterialiser(materialiser, batchSize, batchTimeout);
		}
	}
}
