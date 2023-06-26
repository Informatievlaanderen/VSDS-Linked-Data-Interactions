package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials.ClientCredentialsConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.ApiKeyConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.DefaultConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.ExponentialRandomBackoffConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.RetryExecutor;

import java.util.List;

public class RequestExecutorFactory {

	public RequestExecutor createApiKeyExecutor(String keyHeader, String key) {
		return new ApiKeyConfig(keyHeader, key).createRequestExecutor();
	}

	public RequestExecutor createNoAuthExecutor() {
		return new DefaultConfig().createRequestExecutor();
	}

	public RequestExecutor createClientCredentialsExecutor(String clientId,
			String secret,
			String tokenEndpoint) {
		return new ClientCredentialsConfig(clientId, secret, tokenEndpoint).createRequestExecutor();
	}

	public RequestExecutor createRetryExecutor(RequestExecutor requestExecutor, int maxAttempts,
			List<Integer> statusesToRetry) {
		var exponentialRandomBackoffConfig = new ExponentialRandomBackoffConfig(maxAttempts, statusesToRetry);
		return new RetryExecutor(requestExecutor, exponentialRandomBackoffConfig.createRetryConfig());
	}

}
