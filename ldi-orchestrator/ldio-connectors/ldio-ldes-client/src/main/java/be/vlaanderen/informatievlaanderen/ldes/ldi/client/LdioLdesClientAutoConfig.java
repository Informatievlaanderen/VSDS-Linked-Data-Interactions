package be.vlaanderen.informatievlaanderen.ldes.ldi.client;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import ldes.client.requestexecutor.domain.valueobjects.AuthStrategy;
import ldes.client.requestexecutor.domain.services.RequestExecutorFactory;
import ldes.client.requestexecutor.executor.RequestExecutor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClientProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClientProperties.API_KEY;
import static ldes.client.requestexecutor.domain.valueobjects.AuthStrategy.*;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioLdesClientAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClient")
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
			RequestExecutor requestExecutor = getRequestExecutor(properties);
			LdesClientRunner ldesClientRunner = new LdesClientRunner(requestExecutor, properties, componentExecutor);
			return new LdioLdesClient(componentExecutor, ldesClientRunner);
		}

		private RequestExecutor getRequestExecutor(ComponentProperties componentProperties) {
			Optional<AuthStrategy> authentication = AuthStrategy
					.from(componentProperties.getOptionalProperty(AUTH_TYPE).orElse(NO_AUTH.name()));
			if (authentication.isPresent()) {
				RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();
				return switch (authentication.get()) {
					case NO_AUTH -> requestExecutorFactory.createNoAuthExecutor();
					case API_KEY ->
						requestExecutorFactory.createApiKeyExecutor(
								componentProperties.getOptionalProperty(API_KEY_HEADER).orElse("X-API-KEY"),
								componentProperties.getProperty(API_KEY));
					case OAUTH2_CLIENT_CREDENTIALS ->
						requestExecutorFactory.createClientCredentialsExecutor(
								componentProperties.getProperty(CLIENT_ID),
								componentProperties.getProperty(CLIENT_SECRET),
								componentProperties.getProperty(TOKEN_ENDPOINT),
								componentProperties.getProperty(OAUTH_SCOPE));
				};
			}
			throw new UnsupportedOperationException(
					"Requested authentication not available: " + authentication);
		}
	}

}
