package be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.ratelimiter.RateLimiterConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.RetryConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorDecorator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy.NO_AUTH;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.RequestExecutorProperties.*;

/**
 * Creates a RequestExecutor based on the config provided using LDIO
 * ComponentProperties.
 */
public class LdioRequestExecutorSupplier {

	public static final String DEFAULT_API_KEY_HEADER = "X-API-KEY";

	private final RequestExecutorFactory requestExecutorFactory;

	public LdioRequestExecutorSupplier() {
		this(new RequestExecutorFactory());
	}

	public LdioRequestExecutorSupplier(RequestExecutorFactory requestExecutorFactory) {
		this.requestExecutorFactory = requestExecutorFactory;
	}

	public RequestExecutor getRequestExecutor(ComponentProperties props) {
		final RequestExecutor baseRequestExecutor = getBaseRequestExecutor(props);
		Retry retry = getRetry(props);
		RateLimiter rateLimiter = getRateLimiter(props);
		return RequestExecutorDecorator.decorate(baseRequestExecutor).with(retry).with(rateLimiter).get();
	}

	private RateLimiter getRateLimiter(ComponentProperties props) {
		boolean rateLimitEnabled = props.getOptionalBoolean(RATE_LIMIT_ENABLED).orElse(Boolean.TRUE);
		if (rateLimitEnabled) {
			int maxRequestsPerMinute = props.getOptionalInteger(MAX_REQUESTS_PER_MINUTE).orElse(500);
			return RateLimiterConfig.limitPerMinute(maxRequestsPerMinute).getRateLimiter();
		} else {
			return null;
		}
	}

	private Retry getRetry(ComponentProperties props) {
		boolean retriesEnabled = props.getOptionalBoolean(RETRIES_ENABLED).orElse(Boolean.TRUE);
		if (retriesEnabled) {
			int maxRetries = props.getOptionalInteger(MAX_RETRIES).orElse(5);
			List<Integer> statusesToRetry = props.getOptionalProperty(STATUSES_TO_RETRY)
					.map(csv -> Stream.of(csv.split(",")).map(String::trim).map(Integer::parseInt).toList())
					.orElse(new ArrayList<>());
			return RetryConfig.of(maxRetries, statusesToRetry).getRetry();
		} else {
			return null;
		}
	}

	private RequestExecutor getBaseRequestExecutor(ComponentProperties componentProperties) {
		Optional<AuthStrategy> authentication = AuthStrategy
				.from(componentProperties.getOptionalProperty(AUTH_TYPE).orElse(NO_AUTH.name()));
		if (authentication.isPresent()) {
			return switch (authentication.get()) {
				case NO_AUTH -> requestExecutorFactory.createNoAuthExecutor();
				case API_KEY ->
					requestExecutorFactory
							.createApiKeyExecutor(
									componentProperties.getOptionalProperty(API_KEY_HEADER)
											.orElse(DEFAULT_API_KEY_HEADER),
									componentProperties.getProperty(API_KEY));
				case OAUTH2_CLIENT_CREDENTIALS ->
					requestExecutorFactory.createClientCredentialsExecutor(
							componentProperties.getProperty(CLIENT_ID),
							componentProperties.getProperty(CLIENT_SECRET),
							componentProperties.getProperty(TOKEN_ENDPOINT));
			};
		}
		throw new UnsupportedOperationException("Requested authentication not available: "
				+ componentProperties.getOptionalProperty(AUTH_TYPE).orElse("No auth type provided"));
	}

}
