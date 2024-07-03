package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient;
import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusConsumer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioObserver;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PersistenceProperties.KEEP_STATE;

@Configuration
public class LdioLdesClientAutoConfig {
	@SuppressWarnings("java:S6830")
	@Bean(LdioLdesClient.NAME)
	public LdioInputConfigurator ldioConfigurator(ClientStatusService clientStatusService, ObservationRegistry observationRegistry) {
		return new LdioLdesClientConfigurator(clientStatusService, observationRegistry);
	}

	public static class LdioLdesClientConfigurator implements LdioInputConfigurator {
		private final ClientStatusService clientStatusService;

		private final ObservationRegistry observationRegistry;

		public LdioLdesClientConfigurator(ClientStatusService clientStatusService, ObservationRegistry observationRegistry) {
			this.clientStatusService = clientStatusService;
			this.observationRegistry = observationRegistry;
		}

		@Override
		public LdioInput configure(LdiAdapter adapter, ComponentExecutor componentExecutor,
								   ApplicationEventPublisher applicationEventPublisher,
								   ComponentProperties properties) {
			String pipelineName = properties.getPipelineName();
			final var requestExecutorFactory = new RequestExecutorFactory(false);
			final var requestExecutor = new LdioRequestExecutorSupplier(requestExecutorFactory).getRequestExecutor(properties);
			final var clientStatusConsumer = new ClientStatusConsumer(pipelineName, clientStatusService);
			final MemberSupplier memberSupplier = new MemberSupplierFactory(properties, requestExecutor, clientStatusConsumer).getMemberSupplier();
			final boolean keepState = properties.getOptionalBoolean(KEEP_STATE).orElse(false);
			final LdioObserver ldioObserver = LdioObserver.register(LdioLdesClient.NAME, pipelineName, observationRegistry);
			final var ldesClient = new LdioLdesClient(componentExecutor, ldioObserver, memberSupplier, applicationEventPublisher, keepState, clientStatusConsumer);
			ldesClient.start();
			return ldesClient;
		}

		@Override
		public boolean isAdapterRequired() {
			return false;
		}
	}
}
