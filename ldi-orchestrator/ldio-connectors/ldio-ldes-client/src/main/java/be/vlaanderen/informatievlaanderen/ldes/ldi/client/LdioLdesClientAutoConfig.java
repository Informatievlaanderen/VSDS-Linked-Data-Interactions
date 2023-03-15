package be.vlaanderen.informatievlaanderen.ldes.ldi.client;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import ldes.client.requestexecutor.domain.services.RequestExecutorFactory;
import ldes.client.requestexecutor.domain.valueobjects.AuthStrategy;
import ldes.client.requestexecutor.executor.RequestExecutor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClientProperties.*;
import static ldes.client.requestexecutor.domain.valueobjects.AuthStrategy.NO_AUTH;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioLdesClientAutoConfig {

	public static final String DEFAULT_API_KEY_HEADER = "X-API-KEY";

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClient")
	public LdioConfigurator ldioConfigurator(ComponentExecutor componentExecutor) {
		return new LdioLdesClientConfigurator(componentExecutor);
	}

	public static class LdioLdesClientConfigurator implements LdioConfigurator {
		private final ComponentExecutor componentExecutor;
		private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();

		public LdioLdesClientConfigurator(ComponentExecutor componentExecutor) {
			this.componentExecutor = componentExecutor;
		}

		@Override
		public LdiComponent configure(ComponentProperties properties) {
			RequestExecutor requestExecutor = getRequestExecutorWithPossibleRetry(properties);
			LdesClientRunner ldesClientRunner = new LdesClientRunner(requestExecutor, properties, componentExecutor);
			return new LdioLdesClient(componentExecutor, ldesClientRunner);
		}

		private RequestExecutor getRequestExecutorWithPossibleRetry(ComponentProperties props) {
			final RequestExecutor requestExecutor = getRequestExecutor(props);
			boolean retriesEnabled = props.getOptionalBoolean(RETRIES_ENABLED).orElse(Boolean.FALSE);
			if (retriesEnabled) {
				int maxRetries = props.getOptionalInteger(MAX_RETRIES).orElse(Integer.MAX_VALUE);
				return requestExecutorFactory.createRetryExecutor(requestExecutor, maxRetries);
			} else {
				return requestExecutor;
			}
		}

		private RequestExecutor getRequestExecutor(ComponentProperties componentProperties) {
			return AuthStrategy
					.from(componentProperties.getOptionalProperty(AUTH_TYPE).orElse(NO_AUTH.name()))
					.map(authentication -> switch (authentication) {
						case NO_AUTH -> requestExecutorFactory.createNoAuthExecutor();
						case API_KEY ->
							requestExecutorFactory.createApiKeyExecutor(
									componentProperties.getOptionalProperty(API_KEY_HEADER)
											.orElse(DEFAULT_API_KEY_HEADER),
									componentProperties.getProperty(API_KEY));
						case OAUTH2_CLIENT_CREDENTIALS ->
							requestExecutorFactory.createClientCredentialsExecutor(
									componentProperties.getProperty(CLIENT_ID),
									componentProperties.getProperty(CLIENT_SECRET),
									componentProperties.getProperty(TOKEN_ENDPOINT));
					}).orElseThrow(() -> new UnsupportedOperationException("Requested authentication not available: "
							+ componentProperties.getOptionalProperty(AUTH_TYPE).orElse(null)));
		}

	}

}
