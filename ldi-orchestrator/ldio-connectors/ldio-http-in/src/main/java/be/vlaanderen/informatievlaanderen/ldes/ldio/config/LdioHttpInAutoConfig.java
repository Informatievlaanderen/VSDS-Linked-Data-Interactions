package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.exception.LdiAdapterMissingException.verifyAdapterPresent;

@Configuration
public class LdioHttpInAutoConfig {
	@SuppressWarnings("java:S6830")
	@Bean(LdioHttpIn.NAME)
	public LdioHttpInConfigurator ldioConfigurator(ConfigurableApplicationContext configContext,
												   ObservationRegistry observationRegistry) {
		return new LdioHttpInConfigurator(observationRegistry);
	}

	public static class LdioHttpInConfigurator implements LdioInputConfigurator {

		private final ObservationRegistry observationRegistry;

		public LdioHttpInConfigurator(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
		}

		@Override
		public Object configure(LdiAdapter adapter,
								ComponentExecutor executor,
								ComponentProperties config) {
			String pipelineName = config.getProperty(PIPELINE_NAME);
			verifyAdapterPresent(pipelineName, adapter);

			LdioHttpIn ldioHttpIn = new LdioHttpIn(pipelineName, executor, adapter, observationRegistry);

			return ldioHttpIn.mapping();
		}
	}
}
