package be.vlaanderen.informatievlaanderen.ldes.client.cli.config;

import be.vlaanderen.informatievlaanderen.ldes.client.cli.model.EndpointBehaviour;
import be.vlaanderen.informatievlaanderen.ldes.client.cli.services.LdesClientCli;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.EndpointRequester;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.*;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioLdesClientAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.client.cli.services.LdesClientCli")
	public LdioConfigurator ldioConfigurator(ComponentExecutor componentExecutor) {
		return new LdioLdesClientConfigurator(componentExecutor);
	}

	public static class LdioLdesClientConfigurator implements LdioConfigurator {
		private final ComponentExecutor componentExecutor;

		public LdioLdesClientConfigurator(ComponentExecutor componentExecutor) {
			this.componentExecutor = componentExecutor;
		}

		@Override
		public LdiComponent configure(ComponentProperties properties) {
			String targetUrl = properties.getProperty("url");
			Lang sourceFormat = properties.getOptionalProperty("sourceFormat")
					.map(RDFLanguages::nameToLang)
					.orElse(DEFAULT_DATA_SOURCE_FORMAT);
			Long expirationInterval = properties.getOptionalProperty("expiration-interval")
					.map(Long::valueOf)
					.orElse(DEFAULT_FRAGMENT_EXPIRATION_INTERVAL);
			Long pollingInterval = properties.getOptionalProperty("polling-interval")
					.map(Long::valueOf)
					.orElse(DEFAULT_POLLING_INTERVAL);
			EndpointBehaviour endpointBehaviour = properties.getOptionalProperty("endpoint-behaviour")
					.map(EndpointBehaviour::valueOf)
					.orElse(EndpointBehaviour.STOPPING);

			return new LdesClientCli(newSingleThreadExecutor(), new EndpointRequester(), componentExecutor, targetUrl,
					sourceFormat,
					expirationInterval, pollingInterval, endpointBehaviour);
		}
	}

}
