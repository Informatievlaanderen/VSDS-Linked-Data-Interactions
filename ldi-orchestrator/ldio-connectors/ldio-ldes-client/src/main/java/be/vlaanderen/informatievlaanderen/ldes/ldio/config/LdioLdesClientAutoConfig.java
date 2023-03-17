package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdesClientRunner;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import ldes.client.requestexecutor.domain.services.RequestExecutorFactory;
import ldes.client.requestexecutor.domain.valueobjects.AuthStrategy;
import ldes.client.requestexecutor.executor.RequestExecutor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static ldes.client.requestexecutor.domain.valueobjects.AuthStrategy.NO_AUTH;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioLdesClientAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClient")
	public LdioInputConfigurator ldioConfigurator() {
		return new LdioLdesClientConfigurator();
	}

	public static class LdioLdesClientConfigurator implements LdioInputConfigurator {
		@Override
		public Object configure(LdiAdapter adapter, ComponentExecutor executor,
				ComponentProperties properties) {
			RequestExecutor requestExecutor = getRequestExecutor(properties);
			LdesClientRunner ldesClientRunner = new LdesClientRunner(requestExecutor, properties, executor);
			return new LdioLdesClient(executor, ldesClientRunner);
		}

		private RequestExecutor getRequestExecutor(ComponentProperties componentProperties) {
			Optional<AuthStrategy> authentication = AuthStrategy
					.from(componentProperties.getOptionalProperty(LdioLdesClientProperties.AUTH_TYPE)
							.orElse(NO_AUTH.name()));
			if (authentication.isPresent()) {
				RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();
				return switch (authentication.get()) {
					case NO_AUTH -> requestExecutorFactory.createNoAuthExecutor();
					case API_KEY ->
						requestExecutorFactory.createApiKeyExecutor(
								componentProperties.getOptionalProperty(LdioLdesClientProperties.API_KEY_HEADER)
										.orElse("X-API-KEY"),
								componentProperties.getProperty(LdioLdesClientProperties.API_KEY));
					case OAUTH2_CLIENT_CREDENTIALS ->
						requestExecutorFactory.createClientCredentialsExecutor(
								componentProperties.getProperty(LdioLdesClientProperties.CLIENT_ID),
								componentProperties.getProperty(LdioLdesClientProperties.CLIENT_SECRET),
								componentProperties.getProperty(LdioLdesClientProperties.TOKEN_ENDPOINT));
				};
			}
			throw new UnsupportedOperationException(
					"Requested authentication not available: " + authentication);
		}
	}

}
