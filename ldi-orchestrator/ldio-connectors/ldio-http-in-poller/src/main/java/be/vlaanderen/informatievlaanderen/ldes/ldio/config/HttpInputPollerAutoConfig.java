package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.HttpInputPoller;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.HttpInputPollerProperties.*;

@Configuration
public class HttpInputPollerAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInPoller")
	public HttpInputPollerConfigurator httpInputPollerConfigurator() {
		return new HttpInputPollerConfigurator();
	}

	public static class HttpInputPollerConfigurator implements LdioInputConfigurator {

		@Override
		public HttpInputPoller configure(LdiAdapter adapter, ComponentExecutor executor,
				ComponentProperties properties) {
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

			HttpInputPoller httpInputPoller = new HttpInputPoller(executor, adapter, endpoints, continueOnFail);
			httpInputPoller.schedulePoller(seconds);

			return httpInputPoller;
		}

	}
}
