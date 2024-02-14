package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInProcess;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.event.HttpInPipelineCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInProcess.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.exception.LdiAdapterMissingException.verifyAdapterPresent;

@Configuration
public class LdioHttpInAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public LdioHttpInConfigurator ldioConfigurator(ApplicationEventPublisher eventPublisher,
	                                               ObservationRegistry observationRegistry) {
		return new LdioHttpInConfigurator(eventPublisher, observationRegistry);
	}

	public static class LdioHttpInConfigurator implements LdioInputConfigurator {
		private final ApplicationEventPublisher eventPublisher;

		private final ObservationRegistry observationRegistry;

		public LdioHttpInConfigurator(ApplicationEventPublisher eventPublisher, ObservationRegistry observationRegistry) {
			this.eventPublisher = eventPublisher;
			this.observationRegistry = observationRegistry;
		}

		@Override
		public Object configure(LdiAdapter adapter,
		                        ComponentExecutor executor,
		                        ComponentProperties config) {
			String pipelineName = config.getProperty(PIPELINE_NAME);
			verifyAdapterPresent(pipelineName, adapter);

			LdioHttpInProcess ldioHttpIn = new LdioHttpInProcess(pipelineName, executor, adapter, observationRegistry);

			eventPublisher.publishEvent(new HttpInPipelineCreatedEvent(pipelineName, ldioHttpIn));
			return ldioHttpIn;
		}
	}
}
