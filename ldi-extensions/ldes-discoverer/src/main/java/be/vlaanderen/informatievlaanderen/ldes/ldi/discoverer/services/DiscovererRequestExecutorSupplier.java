package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.config.RequestExecutorProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.ratelimiter.RateLimiterConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.RetryConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorDecorator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DiscovererRequestExecutorSupplier {
	private final RequestExecutorProperties properties;
	private final RequestExecutorFactory requestExecutorFactory;

	public DiscovererRequestExecutorSupplier(RequestExecutorProperties properties) {
		this(properties, new RequestExecutorFactory(false));
	}

	public DiscovererRequestExecutorSupplier(RequestExecutorProperties properties, RequestExecutorFactory requestExecutorFactory) {
		this.properties = properties;
		this.requestExecutorFactory = requestExecutorFactory;
	}

	public RequestExecutor createRequestExecutor() {
		final RequestExecutorDecorator decorator = RequestExecutorDecorator.decorate(getBaseRequestExecutor());
		getRateLimiter().ifPresent(decorator::with);
		getRetry().ifPresent(decorator::with);
		return decorator.get();
	}

	private Optional<RateLimiter> getRateLimiter() {
		return properties.getRateLimit()
				.map(rateLimit -> RateLimiterConfig.limitForPeriod(rateLimit, properties.getRateLimitPeriod()))
				.map(RateLimiterConfig::getRateLimiter);
	}

	private Optional<Retry> getRetry() {
		if (properties.isRetryingDisabled()) {
			return Optional.empty();
		}
		return Optional.of(RetryConfig.of(properties.getRetryLimit(), properties.getRetryStatuses()).getRetry());
	}

	private RequestExecutor getBaseRequestExecutor() {
		final Collection<Header> headers = properties.getHeaders();
		return switch (properties.getAuthStrategy()) {
			case NO_AUTH -> requestExecutorFactory.createNoAuthExecutor(headers);
			case API_KEY -> {
				List<Header> headersWithApiKey = new ArrayList<>(headers);
				headersWithApiKey.add(new BasicHeader(properties.getApiKeyHeader(), properties.getApiKey()));
				yield requestExecutorFactory.createNoAuthExecutor(headersWithApiKey);
			}
			case OAUTH2_CLIENT_CREDENTIALS -> requestExecutorFactory.createClientCredentialsExecutor(
					headers,
					properties.getClientId(),
					properties.getClientSecret(),
					properties.getTokenEndpoint(),
					properties.getAuthScope().orElse(null));
		};
	}

}
