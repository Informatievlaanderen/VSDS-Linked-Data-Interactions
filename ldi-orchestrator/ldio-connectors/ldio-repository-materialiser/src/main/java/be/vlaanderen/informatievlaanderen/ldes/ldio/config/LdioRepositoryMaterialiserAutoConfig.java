package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRepositoryMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.LdioPipelineEventsListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRepositoryMaterialiser.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioRepositoryMaterialiserProperties.*;

@Configuration
public class LdioRepositoryMaterialiserAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public LdioOutputConfigurator ldiRepoMaterialiserConfigurator(LdioPipelineEventsListener<LdioRepositoryMaterialiser> pipelineEventsListener) {
		return new LdiRepoMaterialiserProcessorConfigurator(pipelineEventsListener);
	}

	public static class LdiRepoMaterialiserProcessorConfigurator implements LdioOutputConfigurator {
		private final LdioPipelineEventsListener<LdioRepositoryMaterialiser> pipelineEventsListener;

		public LdiRepoMaterialiserProcessorConfigurator(LdioPipelineEventsListener<LdioRepositoryMaterialiser> pipelineEventsListener) {
			this.pipelineEventsListener = pipelineEventsListener;
		}

		@Override
		public LdiComponent configure(ComponentProperties config) {
			final String hostUrl = config.getProperty(SPARQL_HOST);
			final String repositoryId = config.getProperty(REPOSITORY_ID);
			final String namedGraph = config.getOptionalProperty(NAMED_GRAPH).orElse("");
			final Materialiser materialiser = new Materialiser(hostUrl, repositoryId, namedGraph);
			final int batchSize = config.getOptionalInteger(BATCH_SIZE).orElse(BATCH_SIZE_DEFAULT);
			final int batchTimeout = config.getOptionalInteger(BATCH_TIMEOUT).orElse(BATCH_TIMEOUT_DEFAULT);
			final LdioRepositoryMaterialiser ldioRepositoryMaterialiser =  new LdioRepositoryMaterialiser(materialiser, batchSize, batchTimeout);
			pipelineEventsListener.registerComponent(config.getPipelineName(), ldioRepositoryMaterialiser);
			return ldioRepositoryMaterialiser;
		}
	}
}
