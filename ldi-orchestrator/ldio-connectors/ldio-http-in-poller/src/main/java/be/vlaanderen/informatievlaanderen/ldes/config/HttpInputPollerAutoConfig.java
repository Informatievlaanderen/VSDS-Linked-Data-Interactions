package be.vlaanderen.informatievlaanderen.ldes.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.HttpInputPoller;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.SchedulerExecutionException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.SchedulerInterruptedException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
		public Object configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties properties) {
			String endpoint = properties.getProperty("pipelines.input.config.targetUrl");
			String pollingInterval = properties.getProperty("pipelines.input.config.interval");

			long seconds;
			try {
				seconds = Duration.parse(pollingInterval).getSeconds();
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("Invalid argument for iso 8601 duration");
			}
			WebClient client = WebClient.create(endpoint);

			HttpInputPoller httpInputPoller = new HttpInputPoller(executor, adapter, client);


			try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()
			) {
				return scheduler.scheduleAtFixedRate(httpInputPoller::poll, 0, seconds, TimeUnit.SECONDS)
						.get();
			} catch (ExecutionException e) {
				throw new SchedulerExecutionException(e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new SchedulerInterruptedException(e);
			}
		}
	}
}
