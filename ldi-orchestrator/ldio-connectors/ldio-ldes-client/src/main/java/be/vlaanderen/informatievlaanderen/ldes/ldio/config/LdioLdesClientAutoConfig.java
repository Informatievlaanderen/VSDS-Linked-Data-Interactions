package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.treenodesupplier.MemberSupplier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
			RequestExecutor requestExecutor = new LdioRequestExecutorSupplier().getRequestExecutor(properties);
			final MemberSupplier memberSupplier = new MemberSupplierFactory(properties, requestExecutor).getMemberSupplier();
			var ldesClient = new LdioLdesClient(pipelineName, componentExecutor, observationRegistry, memberSupplier, applicationEventPublisher);
			ldesClient.start();
			return ldesClient;
		}

	}
}
