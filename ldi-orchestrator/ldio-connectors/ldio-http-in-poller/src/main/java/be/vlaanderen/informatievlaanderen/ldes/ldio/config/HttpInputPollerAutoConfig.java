package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.HttpInputPoller;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.HttpInputPollerProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;

@Configuration
public class HttpInputPollerAutoConfig {
	private static final String NAME = "be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInPoller";

	private static final LdioRequestExecutorSupplier ldioRequestExecutorSupplier = new LdioRequestExecutorSupplier();

	@Bean(NAME)
	public HttpInputPollerConfigurator httpInputPollerConfigurator() {
		return new HttpInputPollerConfigurator();
	}

	public static class HttpInputPollerConfigurator implements LdioInputConfigurator {

		@Override
		public HttpInputPoller configure(LdiAdapter adapter, ComponentExecutor executor,
				ComponentProperties properties) {
			String pipelineName = properties.getProperty(PIPELINE_NAME);
			List<String> endpoints = properties.getPropertyList(URL);

			String pollingInterval = properties.getProperty(INTERVAL);
			boolean continueOnFail = properties.getOptionalBoolean(CONTINUE_ON_FAIL).orElse(true);

			long seconds;
			try {
				seconds = Duration.parse(pollingInterval).getSeconds();
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("Invalid config for the ldio http in poller: " + INTERVAL
						+ " cannot have following value: " + pollingInterval);
			}

			var requestExecutor = ldioRequestExecutorSupplier.getRequestExecutor(properties);
			var httpInputPoller = new HttpInputPoller(NAME, pipelineName, executor, adapter, endpoints, continueOnFail, requestExecutor);
			httpInputPoller.schedulePoller(seconds);

			return httpInputPoller;
		}

	}
}
