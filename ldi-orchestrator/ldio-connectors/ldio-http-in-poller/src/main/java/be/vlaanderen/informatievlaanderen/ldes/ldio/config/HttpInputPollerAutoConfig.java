package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.HttpInputPoller;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.HttpInputPoller.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.HttpInputPollerProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;

@Configuration
public class HttpInputPollerAutoConfig {
	private static final LdioRequestExecutorSupplier ldioRequestExecutorSupplier = new LdioRequestExecutorSupplier();

	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public HttpInputPollerConfigurator httpInputPollerConfigurator(ObservationRegistry observationRegistry) {
		return new HttpInputPollerConfigurator(observationRegistry);
	}

	public static class HttpInputPollerConfigurator implements LdioInputConfigurator {
		private final ObservationRegistry observationRegistry;

		public HttpInputPollerConfigurator(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
		}

		@Override
		public HttpInputPoller configure(LdiAdapter adapter, ComponentExecutor executor,
		                                 ComponentProperties properties) {
			String pipelineName = properties.getProperty(PIPELINE_NAME);
			List<String> endpoints = properties.getPropertyList(URL);

			boolean continueOnFail = properties.getOptionalBoolean(CONTINUE_ON_FAIL).orElse(true);

			var requestExecutor = ldioRequestExecutorSupplier.getRequestExecutor(properties);

			var httpInputPoller = new HttpInputPoller(pipelineName, executor, adapter, observationRegistry, endpoints, continueOnFail, requestExecutor);

			httpInputPoller.schedulePoller(getPollingInterval(properties));

			return httpInputPoller;
		}

		private PollingInterval getPollingInterval(ComponentProperties properties) {
			Optional<String> expression = properties.getOptionalProperty(CRON);

			return expression.map(PollingInterval::withCron)
					.orElseGet(() -> PollingInterval.withInterval(properties.getProperty(INTERVAL)));
		}
	}

}
