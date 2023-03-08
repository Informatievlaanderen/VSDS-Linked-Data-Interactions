package ldes.client.requestexecutor.executor.retry;

import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.RequestExecutor;

public class RetryExecutor implements RequestExecutor {

	private final RequestExecutor requestExecutor;
	private final RetryConfig retryConfig;

	public RetryExecutor(RequestExecutor requestExecutor, RetryConfig retryConfig) {
		this.requestExecutor = requestExecutor;
		this.retryConfig = retryConfig;
	}

	// TODO VSDSPUB-521: 8/03/2023 test
	@Override
	public Response execute(Request request) {
		return RetryRegistry
				.of(retryConfig)
				.retry("retry-http-requests")
				.executeSupplier(() -> requestExecutor.execute(request));
	}

}
