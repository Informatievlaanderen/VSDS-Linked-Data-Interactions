package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRepositoryMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldio.PipelineEventsListener;
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
	public LdioOutputConfigurator ldiRepoMaterialiserConfigurator(PipelineEventsListener pipelineEventsListener) {
		return new LdiRepoMaterialiserProcessorConfigurator(pipelineEventsListener);
	}

	public static class LdiRepoMaterialiserProcessorConfigurator implements LdioOutputConfigurator {
		private final PipelineEventsListener pipelineEventsListener;

		public LdiRepoMaterialiserProcessorConfigurator(PipelineEventsListener pipelineEventsListener) {
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
			pipelineEventsListener.registerMaterialiser(config.getPipelineName(), ldioRepositoryMaterialiser);
			return ldioRepositoryMaterialiser;
		}
	}
}
