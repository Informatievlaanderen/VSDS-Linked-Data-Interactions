package ldes.client.requestexecutor.domain.services;

import ldes.client.requestexecutor.domain.valueobjects.executorsupplier.ApiKeyConfig;
import ldes.client.requestexecutor.domain.valueobjects.executorsupplier.ClientCredentialsConfig;
import ldes.client.requestexecutor.domain.valueobjects.executorsupplier.DefaultConfig;
import ldes.client.requestexecutor.domain.valueobjects.executorsupplier.ExponentialRandomBackoffConfig;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.requestexecutor.executor.retry.RetryExecutor;

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

	public RequestExecutor createRetryExecutor(RequestExecutor requestExecutor, int maxAttempts) {
		return new RetryExecutor(requestExecutor, new ExponentialRandomBackoffConfig(maxAttempts).createRetryConfig());
	}

}
