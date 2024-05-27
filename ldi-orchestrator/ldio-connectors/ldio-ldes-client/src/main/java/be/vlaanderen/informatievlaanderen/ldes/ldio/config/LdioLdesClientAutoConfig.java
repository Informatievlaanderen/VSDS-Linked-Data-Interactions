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
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioLdesClientAutoConfig {
	@SuppressWarnings("java:S6830")
	@Bean(LdioLdesClient.NAME)
	public LdioInputConfigurator ldioConfigurator(ObservationRegistry observationRegistry, ApplicationEventPublisher eventPublisher) {
		return new LdioLdesClientConfigurator(observationRegistry, eventPublisher);
	}

	public static class LdioLdesClientConfigurator implements LdioInputConfigurator {

		private final ObservationRegistry observationRegistry;
		private final ApplicationEventPublisher eventPublisher;

		public LdioLdesClientConfigurator(ObservationRegistry observationRegistry, ApplicationEventPublisher eventPublisher) {
			this.observationRegistry = observationRegistry;
			this.eventPublisher = eventPublisher;
		}

		@Override
		public LdioInput configure(LdiAdapter adapter, ComponentExecutor componentExecutor,
								   ComponentProperties properties) {
			String pipelineName = properties.getPipelineName();
			final var requestExecutorFactory = new RequestExecutorFactory(false);
			final var requestExecutor = new LdioRequestExecutorSupplier(requestExecutorFactory).getRequestExecutor(properties);
			final MemberSupplier memberSupplier = new MemberSupplierFactory(properties, requestExecutor).getMemberSupplier();
			return new LdioLdesClient(componentExecutor, pipelineName, observationRegistry, memberSupplier, eventPublisher);
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
