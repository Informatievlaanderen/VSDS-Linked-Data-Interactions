package be.vlaanderen.informatievlaanderen.ldes.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.poller.HttpInputPoller;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties
public class HttpInputPollerConfig {

	@Bean
	public HttpInputPollerConfigurator httpInputPollerConfigurator() {
		return new HttpInputPollerConfigurator();
	}

	public static class HttpInputPollerConfigurator implements LdioInputConfigurator {

		private static final String ISO_8601_DURATION = "P10M";

		@Override
		public Object configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties properties) {
			String endpoint = properties.getProperty("pipelines.input.config.targetUrl");

			HttpInputPoller httpInputPoller = new HttpInputPoller(executor, adapter, endpoint);


			try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()
			) {
				return scheduler.scheduleAtFixedRate(httpInputPoller::poll, 0, Duration.parse(ISO_8601_DURATION).getSeconds(), TimeUnit.SECONDS)
						.get();
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		}
	}
}
