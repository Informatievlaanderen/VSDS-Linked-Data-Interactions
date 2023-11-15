package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.EdcConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.MemoryTokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.MemoryTransferService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.DefaultConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdesClientRunner;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnectorApi;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;

@Configuration
public class LdioLdesClientConnectorAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnector")
	public LdioHttpInConfigurator ldioConfigurator() {
		return new LdioHttpInConfigurator();
	}

	// TODO TVB: 13/11/23 test
	// TODO TVB: 13/11/23 cleanup
	public static class LdioHttpInConfigurator implements LdioInputConfigurator {

		public static final String CONNECTOR_TRANSFER_URL = "connector-transfer-url";

		private final StatePersistenceFactory statePersistenceFactory = new StatePersistenceFactory();
		private final RequestExecutor baseRequestExecutor = new DefaultConfig().createRequestExecutor();

		public void initClient(ComponentExecutor componentExecutor,
							   ComponentProperties properties,
							   MemoryTokenService tokenService) {
			final var edcRequestExecutor = new EdcConfig(baseRequestExecutor, tokenService).createRequestExecutor();
			final StatePersistence statePersistence = statePersistenceFactory.getStatePersistence(properties);
			final LdesClientRunner ldesClientRunner =
					new LdesClientRunner(edcRequestExecutor, properties, componentExecutor, statePersistence);

			// starts the client
			new LdioLdesClient(componentExecutor, ldesClientRunner);
		}

		@Override
		public Object configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties properties) {
			final var pipelineName = properties.getProperty(PIPELINE_NAME);
			final var connectorTransferUrl = properties.getProperty(CONNECTOR_TRANSFER_URL);
			final var transferService = new MemoryTransferService(baseRequestExecutor, connectorTransferUrl);
			final var tokenService = new MemoryTokenService(transferService);
			initClient(executor, properties, tokenService);
			return new LdioLdesClientConnectorApi(pipelineName, transferService, tokenService).endpoints();
		}
	}
}
