package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.MemoryTokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.MemoryTokenServiceLifecycle;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.MemoryTransferService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects.EdcUrlProxy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnectorApi;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.event.LdesClientConnectorApiCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusConsumer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioObserver;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.eventstreamproperties.EventStreamPropertiesFetcher;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@SuppressWarnings("java:S6830")
@Configuration
public class LdioLdesClientConnectorAutoConfig {

	public static final String NAME = "Ldio:LdesClientConnector";

	@Bean(NAME)
	public LdioInputConfigurator ldioConfigurator(ApplicationEventPublisher eventPublisher,
	                                              ClientStatusService clientStatusService,
	                                              ObservationRegistry observationRegistry) {
		return new LdioClientConnectorConfigurator(eventPublisher, clientStatusService, observationRegistry);
	}

	public static class LdioClientConnectorConfigurator implements LdioInputConfigurator {

		public static final String CONNECTOR_TRANSFER_URL = "connector-transfer-url";
		public static final String PROXY_URL_TO_REPLACE = "proxy-url-to-replace";
		public static final String PROXY_URL_REPLACEMENT = "proxy-url-replacement";
		private final ApplicationEventPublisher eventPublisher;
		private final ClientStatusService clientStatusService;
		private final ObservationRegistry observationRegistry;
		private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory(false);
		private final RequestExecutor baseRequestExecutor = requestExecutorFactory.createNoAuthExecutor();

		public LdioClientConnectorConfigurator(ApplicationEventPublisher eventPublisher, ClientStatusService clientStatusService,
		                                       ObservationRegistry observationRegistry) {
			this.eventPublisher = eventPublisher;
			this.clientStatusService = clientStatusService;
			this.observationRegistry = observationRegistry;
		}

		@Override
		public LdioInput configure(LdiAdapter adapter, ComponentExecutor executor, ApplicationEventPublisher applicationEventPublisher, ComponentProperties properties) {
			final String pipelineName = properties.getPipelineName();
			final LdioLdesClientProperties ldioLdesClientProperties = LdioLdesClientProperties.fromComponentProperties(properties);
			final var connectorTransferUrl = properties.getProperty(CONNECTOR_TRANSFER_URL);
			final var transferService = new MemoryTransferService(baseRequestExecutor, connectorTransferUrl);
			final var memoryTokenServiceLifecycle = new MemoryTokenServiceLifecycle();
			final var tokenService = new MemoryTokenService(transferService, memoryTokenServiceLifecycle);

			final var urlProxy = getEdcUrlProxy(properties);
			final var edcRequestExecutor = requestExecutorFactory.createEdcExecutor(baseRequestExecutor, tokenService,
					urlProxy);
			final var eventStreamPropertiesFetcher = new EventStreamPropertiesFetcher(edcRequestExecutor);
			final var clientStatusConsumer = new ClientStatusConsumer(pipelineName, clientStatusService);
			eventPublisher.publishEvent(new LdesClientConnectorApiCreatedEvent(pipelineName, new LdioLdesClientConnectorApi(transferService, tokenService)));
			final MemberSupplier memberSupplier = new MemberSupplierFactory(
					ldioLdesClientProperties,
					eventStreamPropertiesFetcher,
					edcRequestExecutor,
					clientStatusConsumer
			).getMemberSupplier();

			final boolean keepState = ldioLdesClientProperties.isKeepStateEnabled();
			final LdioObserver ldioObserver = LdioObserver.register(NAME, pipelineName, observationRegistry);
			final var ldesClient = new LdioLdesClient(executor, ldioObserver, memberSupplier,
					applicationEventPublisher, keepState, clientStatusConsumer);
			ldesClient.start();
			return ldesClient;
		}

		@Override
		public boolean isAdapterRequired() {
			return false;
		}

		private static EdcUrlProxy getEdcUrlProxy(ComponentProperties properties) {
			final var proxyUrlToReplace = properties.getOptionalProperty(PROXY_URL_TO_REPLACE).orElse("");
			final var proxyUrlReplacement = properties.getOptionalProperty(PROXY_URL_REPLACEMENT).orElse("");
			return new EdcUrlProxy(proxyUrlToReplace, proxyUrlReplacement);
		}
	}
}
