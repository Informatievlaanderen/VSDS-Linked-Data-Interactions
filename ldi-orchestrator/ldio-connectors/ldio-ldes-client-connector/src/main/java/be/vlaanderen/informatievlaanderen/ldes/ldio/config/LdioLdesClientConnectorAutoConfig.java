package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.MemoryTokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.MemoryTransferService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects.EdcUrlProxy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnectorApi;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.event.LdesClientConnectorApiCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.treenodesupplier.MemberSupplier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;

@SuppressWarnings("java:S6830")
@Configuration
public class LdioLdesClientConnectorAutoConfig {

	public static final String NAME = "be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnector";

	@Bean(NAME)
	public LdioInputConfigurator ldioConfigurator(ApplicationEventPublisher eventPublisher,
	                                              ObservationRegistry observationRegistry) {
		return new LdioClientConnectorConfigurator(eventPublisher, observationRegistry);
	}

	public static class LdioClientConnectorConfigurator implements LdioInputConfigurator {

		public static final String CONNECTOR_TRANSFER_URL = "connector-transfer-url";
		public static final String PROXY_URL_TO_REPLACE = "proxy-url-to-replace";
		public static final String PROXY_URL_REPLACEMENT = "proxy-url-replacement";
		private final ApplicationEventPublisher eventPublisher;
		private final ObservationRegistry observationRegistry;
		private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();
		private final RequestExecutor baseRequestExecutor = requestExecutorFactory.createNoAuthExecutor();

		public LdioClientConnectorConfigurator(ApplicationEventPublisher eventPublisher, ObservationRegistry observationRegistry) {
			this.eventPublisher = eventPublisher;
			this.observationRegistry = observationRegistry;
		}

		@Override
		public Object configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties properties) {
			final String pipelineName = properties.getProperty(PIPELINE_NAME);
			final var connectorTransferUrl = properties.getProperty(CONNECTOR_TRANSFER_URL);
			final var transferService = new MemoryTransferService(baseRequestExecutor, connectorTransferUrl);
			final var tokenService = new MemoryTokenService(transferService);

			final var urlProxy = getEdcUrlProxy(properties);
			final var edcRequestExecutor = requestExecutorFactory.createEdcExecutor(baseRequestExecutor, tokenService,
					urlProxy);
			final MemberSupplier memberSupplier = new MemberSupplierFactory(properties, edcRequestExecutor).getMemberSupplier();
			var ldesClient = new LdioLdesClient(pipelineName, executor, observationRegistry, memberSupplier);
			ldesClient.start();
			eventPublisher.publishEvent(new LdesClientConnectorApiCreatedEvent(pipelineName, new LdioLdesClientConnectorApi(transferService, tokenService)));

			return ldesClient;
		}

		private static EdcUrlProxy getEdcUrlProxy(ComponentProperties properties) {
			final var proxyUrlToReplace = properties.getOptionalProperty(PROXY_URL_TO_REPLACE).orElse("");
			final var proxyUrlReplacement = properties.getOptionalProperty(PROXY_URL_REPLACEMENT).orElse("");
			return new EdcUrlProxy(proxyUrlToReplace, proxyUrlReplacement);
		}
	}
}
