package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;

public class ExponentialRandomBackoffConfig {

	private final int maxAttempts;

	public ExponentialRandomBackoffConfig(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	public RetryConfig createRetryConfig() {
		return RetryConfig.<Response>custom()
				.maxAttempts(maxAttempts)
				.intervalFunction(IntervalFunction.ofExponentialRandomBackoff())
				.retryOnResult(response -> response == null || response.getHttpStatus() >= 500)
				.retryOnException(HttpRequestException.class::isInstance)
				.build();
	}

}
