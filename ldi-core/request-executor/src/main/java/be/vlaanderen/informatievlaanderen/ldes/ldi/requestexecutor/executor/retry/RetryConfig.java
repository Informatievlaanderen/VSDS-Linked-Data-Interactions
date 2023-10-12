package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;

import java.util.List;
import java.util.Optional;

public class RetryConfig {

    private final int maxAttempts;
    private final List<Integer> statusesToRetry;

    private RetryConfig(int maxAttempts, List<Integer> statusesToRetry) {
        this.maxAttempts = maxAttempts;
        this.statusesToRetry = statusesToRetry;
    }

    public static RetryConfig of(int maxAttempts, List<Integer> statusesToRetry) {
        return new RetryConfig(maxAttempts, statusesToRetry);
    }

    public Retry getRetry() {
        var exponentialRandomBackoffConfig = new ExponentialRandomBackoffConfig(maxAttempts, statusesToRetry);
        return RetryRegistry
                .of(exponentialRandomBackoffConfig.createRetryConfig())
                .retry("retry-http-requests");
    }

}
