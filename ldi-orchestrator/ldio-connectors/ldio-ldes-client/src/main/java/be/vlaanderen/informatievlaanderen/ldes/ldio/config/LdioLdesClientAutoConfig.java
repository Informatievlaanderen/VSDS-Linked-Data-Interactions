package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.StartedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioObserver;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.KEEP_STATE;

@Configuration
public class LdioLdesClientAutoConfig {
	@SuppressWarnings("java:S6830")
	@Bean(LdioLdesClient.NAME)
	public LdioInputConfigurator ldioConfigurator(ObservationRegistry observationRegistry) {
		return new LdioLdesClientConfigurator(observationRegistry);
	}

	public static class LdioLdesClientConfigurator implements LdioInputConfigurator {

		private final ObservationRegistry observationRegistry;

		public LdioLdesClientConfigurator(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
		}

		@Override
		public LdioInput configure(LdiAdapter adapter, ComponentExecutor componentExecutor,
								   ApplicationEventPublisher applicationEventPublisher,
								   ComponentProperties properties) {
			String pipelineName = properties.getPipelineName();
			final var requestExecutorFactory = new RequestExecutorFactory(false);
			final var requestExecutor = new LdioRequestExecutorSupplier(requestExecutorFactory).getRequestExecutor(properties);
			final MemberSupplier memberSupplier = new MemberSupplierFactory(properties, requestExecutor).getMemberSupplier();
			final boolean keepState = properties.getOptionalBoolean(KEEP_STATE).orElse(false);
			final LdioObserver ldioObserver = LdioObserver.register(LdioLdesClient.NAME, pipelineName, observationRegistry);
			final var ldesClient = new LdioLdesClient(componentExecutor, ldioObserver, memberSupplier, applicationEventPublisher, keepState);
			ldesClient.start();
			return ldesClient;
		}

		@Override
		public boolean isAdapterRequired() {
			return false;
		}

		@Override
		public PipelineStatus getInitialPipelineStatus() {
			return new StartedPipelineStatus();
		}
	}
}
