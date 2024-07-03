package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInProcess;
import be.vlaanderen.informatievlaanderen.ldes.ldio.event.HttpInPipelineCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioObserver;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInProcess.NAME;

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
		public LdioInput configure(LdiAdapter adapter,
								   ComponentExecutor executor,
								   ApplicationEventPublisher applicationEventPublisher,
								   ComponentProperties config) {
			String pipelineName = config.getPipelineName();

			LdioObserver ldioObserver = LdioObserver.register(NAME, pipelineName, observationRegistry);
			LdioHttpInProcess ldioHttpIn = new LdioHttpInProcess(executor, adapter, ldioObserver, applicationEventPublisher);

			eventPublisher.publishEvent(new HttpInPipelineCreatedEvent(pipelineName, ldioHttpIn));
			ldioHttpIn.start();
			return ldioHttpIn;
		}

		@Override
		public boolean isAdapterRequired() {
			return true;
		}
	}
}
