package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Response;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;

public class RetryExecutor implements RequestExecutor {

	private final RequestExecutor requestExecutor;
	private final RetryConfig retryConfig;

	public RetryExecutor(RequestExecutor requestExecutor, RetryConfig retryConfig) {
		this.requestExecutor = requestExecutor;
		this.retryConfig = retryConfig;
	}

	@Override
	public Response execute(Request request) {
		return RetryRegistry
				.of(retryConfig)
				.retry("retry-http-requests")
				.executeSupplier(() -> requestExecutor.execute(request));
	}

}
