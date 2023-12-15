package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.MemoryTokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.MemoryTransferService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects.EdcUrlProxy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnector;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;

@SuppressWarnings("java:S6830")
@Configuration
public class LdioLdesClientConnectorAutoConfig {

	@Bean(LdioLdesClientConnector.NAME)
	public LdioInputConfigurator ldioConfigurator() {
		return new LdioClientConnectorConfigurator();
	}

	public static class LdioClientConnectorConfigurator implements LdioInputConfigurator {

		public static final String CONNECTOR_TRANSFER_URL = "connector-transfer-url";
		public static final String PROXY_URL_TO_REPLACE = "proxy-url-to-replace";
		public static final String PROXY_URL_REPLACEMENT = "proxy-url-replacement";

		private final StatePersistenceFactory statePersistenceFactory = new StatePersistenceFactory();
		private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();
		private final RequestExecutor baseRequestExecutor = requestExecutorFactory.createNoAuthExecutor();

		@Override
		public Object configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties properties) {
			final String pipelineName = properties.getProperty(PIPELINE_NAME);
			final var connectorTransferUrl = properties.getProperty(CONNECTOR_TRANSFER_URL);
			final var transferService = new MemoryTransferService(baseRequestExecutor, connectorTransferUrl);
			final var tokenService = new MemoryTokenService(transferService);

			final var urlProxy = getEdcUrlProxy(properties);
			final var edcRequestExecutor = requestExecutorFactory.createEdcExecutor(baseRequestExecutor, tokenService,
					urlProxy);
			final StatePersistence statePersistence = statePersistenceFactory.getStatePersistence(properties);

			var ldesClientConnector = new LdioLdesClientConnector(pipelineName, transferService, tokenService, edcRequestExecutor, properties,
					executor, statePersistence);

			ldesClientConnector.run();
			return ldesClientConnector.apiEndpoints();
		}

		private static EdcUrlProxy getEdcUrlProxy(ComponentProperties properties) {
			final var proxyUrlToReplace = properties.getOptionalProperty(PROXY_URL_TO_REPLACE).orElse("");
			final var proxyUrlReplacement = properties.getOptionalProperty(PROXY_URL_REPLACEMENT).orElse("");
			return new EdcUrlProxy(proxyUrlToReplace, proxyUrlReplacement);
		}
	}
}
