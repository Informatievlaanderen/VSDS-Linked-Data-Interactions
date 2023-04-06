package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.AuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdesClientRunner;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;

import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.AuthStrategy.NO_AUTH;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.MAX_RETRIES;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.RETRIES_ENABLED;

public class LdioLdesClientConfigurator implements LdioConfigurator {

	public static final String DEFAULT_API_KEY_HEADER = "X-API-KEY";

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
		Optional<AuthStrategy> authentication = AuthStrategy
				.from(componentProperties.getOptionalProperty(LdioLdesClientProperties.AUTH_TYPE)
						.orElse(NO_AUTH.name()));
		if (authentication.isPresent()) {
			return switch (authentication.get()) {
				case NO_AUTH -> requestExecutorFactory.createNoAuthExecutor();
				case API_KEY ->
					requestExecutorFactory.createApiKeyExecutor(
							componentProperties.getOptionalProperty(LdioLdesClientProperties.API_KEY_HEADER)
									.orElse(DEFAULT_API_KEY_HEADER),
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
