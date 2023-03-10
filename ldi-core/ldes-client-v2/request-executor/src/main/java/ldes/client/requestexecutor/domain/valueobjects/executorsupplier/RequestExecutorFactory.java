package ldes.client.requestexecutor.domain.valueobjects.executorsupplier;

import ldes.client.requestexecutor.executor.RequestExecutor;

public class RequestExecutorFactory {
	public RequestExecutor createApiKeyExecutor(String keyHeader, String key) {
		return new ApiKeyConfig(keyHeader, key).createRequestExecutor();
	}

	public RequestExecutor createNoAuthExecutor() {
		return new DefaultConfig().createRequestExecutor();
	}

	public RequestExecutor createClientCredentialsExecutor(String clientId,
			String secret,
			String tokenEndpoint,
			String scope) {
		return new ClientCredentialsConfig(clientId, secret, tokenEndpoint, scope).createRequestExecutor();
	}
}
