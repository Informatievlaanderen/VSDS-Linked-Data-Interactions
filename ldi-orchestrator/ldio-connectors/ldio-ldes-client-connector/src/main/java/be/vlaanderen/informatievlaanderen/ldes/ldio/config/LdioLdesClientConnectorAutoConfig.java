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
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.treenodesupplier.MemberSupplier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.KEEP_STATE;

@SuppressWarnings("java:S6830")
@Configuration
public class LdioLdesClientConnectorAutoConfig {

	public static final String NAME = "Ldio:LdesClientConnector";

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
		public LdioInput configure(LdiAdapter adapter, ComponentExecutor executor, ApplicationEventPublisher applicationEventPublisher, ComponentProperties properties) {
			final String pipelineName = properties.getPipelineName();
			final var connectorTransferUrl = properties.getProperty(CONNECTOR_TRANSFER_URL);
			final var transferService = new MemoryTransferService(baseRequestExecutor, connectorTransferUrl);
			final var tokenService = new MemoryTokenService(transferService);

			final var urlProxy = getEdcUrlProxy(properties);
			final var edcRequestExecutor = requestExecutorFactory.createEdcExecutor(baseRequestExecutor, tokenService,
					urlProxy);
			final MemberSupplier memberSupplier = new MemberSupplierFactory(properties, edcRequestExecutor).getMemberSupplier();
			final boolean keepState = properties.getOptionalBoolean(KEEP_STATE).orElse(false);
			var ldesClient = new LdioLdesClient(pipelineName, executor, observationRegistry, memberSupplier,
					applicationEventPublisher, keepState);
			ldesClient.start();
			eventPublisher.publishEvent(new LdesClientConnectorApiCreatedEvent(pipelineName, new LdioLdesClientConnectorApi(transferService, tokenService, ldesClient)));

			return ldesClient;
		}

		private static EdcUrlProxy getEdcUrlProxy(ComponentProperties properties) {
			final var proxyUrlToReplace = properties.getOptionalProperty(PROXY_URL_TO_REPLACE).orElse("");
			final var proxyUrlReplacement = properties.getOptionalProperty(PROXY_URL_REPLACEMENT).orElse("");
			return new EdcUrlProxy(proxyUrlToReplace, proxyUrlReplacement);
		}
	}
}
