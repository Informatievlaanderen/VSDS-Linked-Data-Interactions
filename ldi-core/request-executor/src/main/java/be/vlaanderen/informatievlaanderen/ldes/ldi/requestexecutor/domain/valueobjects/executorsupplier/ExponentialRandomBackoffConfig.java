package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
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

	// TODO: 16/05/2023 test
	public RetryConfig createRetryConfig() {
		return RetryConfig.<Response>custom()
				.maxAttempts(maxAttempts)
				.intervalBiFunction(new DefaultIntervalFunction(IntervalFunction.ofExponentialRandomBackoff()))
				.retryOnResult(new DefaultRetryOnResultPredicate(statusesToRetry))
				.retryOnException(HttpRequestException.class::isInstance)
				.build();
	}

}
