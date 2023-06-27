package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdesClientRunner;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy.NO_AUTH;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.AUTH_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.MAX_RETRIES;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.RETRIES_ENABLED;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.STATUSES_TO_RETRY;

public class LdioLdesClientConfigurator implements LdioInputConfigurator {

	public static final String DEFAULT_API_KEY_HEADER = "X-API-KEY";

	private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();

	@Override
	public LdiComponent configure(LdiAdapter adapter, ComponentExecutor componentExecutor,
			ComponentProperties properties) {
		RequestExecutor requestExecutor = getRequestExecutorWithPossibleRetry(properties);
		LdesClientRunner ldesClientRunner = new LdesClientRunner(requestExecutor, properties, componentExecutor);
		return new LdioLdesClient(componentExecutor, ldesClientRunner);
	}

	protected RequestExecutor getRequestExecutorWithPossibleRetry(ComponentProperties props) {
		final RequestExecutor requestExecutor = getRequestExecutor(props);
		boolean retriesEnabled = props.getOptionalBoolean(RETRIES_ENABLED).orElse(Boolean.TRUE);
		if (retriesEnabled) {
			int maxRetries = props.getOptionalInteger(MAX_RETRIES).orElse(5);
			List<Integer> statusesToRetry = props.getOptionalProperty(STATUSES_TO_RETRY)
					.map(csv -> Stream.of(csv.split(",")).map(String::trim).map(Integer::parseInt).toList())
					.orElse(new ArrayList<>());
			return requestExecutorFactory.createRetryExecutor(requestExecutor, maxRetries, statusesToRetry);
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
				"Requested authentication not available: "
						+ componentProperties.getOptionalProperty(AUTH_TYPE).orElse("No auth type provided"));
	}

}
