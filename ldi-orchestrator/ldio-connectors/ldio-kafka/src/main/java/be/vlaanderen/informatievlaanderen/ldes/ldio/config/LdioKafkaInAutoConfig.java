package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services.LdioObserver;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn.NAME;

@Configuration
public class LdioKafkaInAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public LdioKafkaInConfigurator ldioConfigurator(ObservationRegistry observationRegistry) {
		return new LdioKafkaInConfigurator(observationRegistry);
	}

	public static class LdioKafkaInConfigurator implements LdioInputConfigurator {
		private final ObservationRegistry observationRegistry;

		public LdioKafkaInConfigurator(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
		}

		@Override
		public LdioInput configure(LdiAdapter adapter, ComponentExecutor executor, ApplicationEventPublisher applicationEventPublisher, ComponentProperties config) {
			final String pipelineName = config.getPipelineName();
			final LdioObserver ldioObserver = LdioObserver.register(NAME, pipelineName, observationRegistry);
			return new LdioKafkaIn(executor, adapter, ldioObserver, applicationEventPublisher, config);
		}

		@Override
		public boolean isAdapterRequired() {
			return true;
		}
	}
}
