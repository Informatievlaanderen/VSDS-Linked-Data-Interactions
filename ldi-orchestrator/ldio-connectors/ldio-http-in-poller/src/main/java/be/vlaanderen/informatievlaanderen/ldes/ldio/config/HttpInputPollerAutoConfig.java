package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.HttpInputPoller;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.HttpInputPollerProperties.*;

@Configuration
@EnableConfigurationProperties
@ComponentScan(value = "be.vlaanderen.informatievlaanderen.ldes")
public class HttpInputPollerAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInPoller")
	public HttpInputPollerConfigurator httpInputPollerConfigurator() {
		return new HttpInputPollerConfigurator();
	}

	public static class HttpInputPollerConfigurator implements LdioInputConfigurator {

		@Override
		public ScheduledExecutorService configure(LdiAdapter adapter, ComponentExecutor executor,
				ComponentProperties properties) {
			String endpoint = properties.getProperty(URL);
			String pollingInterval = properties.getProperty(INTERVAL);
			boolean continueOnFail = properties.getOptionalBoolean(CONTINUE_ON_FAIL).orElse(true);

			long seconds;
			try {
				seconds = Duration.parse(pollingInterval).getSeconds();
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("Invalid config for the ldio http in poller: " + INTERVAL
						+ " cannot have following value: " + pollingInterval);
			}

			HttpInputPoller httpInputPoller = new HttpInputPoller(executor, adapter, endpoint, continueOnFail);

			ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

			scheduler.scheduleAtFixedRate(httpInputPoller::poll, 0, seconds, TimeUnit.SECONDS);

			return scheduler;
		}
	}
}
