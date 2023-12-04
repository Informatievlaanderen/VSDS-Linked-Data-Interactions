package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;

import java.util.List;

public class ExponentialRandomBackoffConfig {

	private final int maxAttempts;
	private final List<Integer> statusesToRetry;

	public ExponentialRandomBackoffConfig(int maxAttempts, List<Integer> statusesToRetry) {
		this.maxAttempts = maxAttempts;
		this.statusesToRetry = statusesToRetry;
	}

	public RetryConfig createRetryConfig() {
		return RetryConfig.<Response>custom()
				.maxAttempts(maxAttempts)
				.intervalBiFunction(new BasicIntervalFunctionDecorator(IntervalFunction.ofExponentialRandomBackoff()))
				.retryOnResult(new HttpStatusRetryPredicate(statusesToRetry))
				.retryOnException(HttpRequestException.class::isInstance)
				.build();
	}

}
