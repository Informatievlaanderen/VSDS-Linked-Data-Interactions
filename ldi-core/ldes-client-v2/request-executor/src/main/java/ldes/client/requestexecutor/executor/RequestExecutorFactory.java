package ldes.client.requestexecutor.executor;

import io.github.resilience4j.retry.RetryConfig;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.retry.RetryExecutor;

import java.io.IOException;
import java.time.Duration;

// TODO VSDSPUB-521: 6/03/2023 refactor in retry story
public class RequestExecutorFactory {

	public RequestExecutor createRetry(RequestExecutor requestExecutor) {
		final RetryConfig config = RetryConfig.<Response>custom()
				.maxAttempts(3)
				.waitDuration(Duration.ofMillis(500))
				.retryOnResult(response -> response == null || response.getHttpStatus() >= 500)
				.retryOnException(IOException.class::isInstance)
				.build();

		return new RetryExecutor(requestExecutor, config);
	}
}
