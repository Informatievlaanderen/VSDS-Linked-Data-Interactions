package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier.ApiKeyConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier.ClientCredentialsConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier.DefaultConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier.retry.ExponentialRandomBackoffConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.RetryExecutor;

import java.util.ArrayList;

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

	// TODO: 16/05/2023 implement custom status list
	public RequestExecutor createRetryExecutor(RequestExecutor requestExecutor, int maxAttempts) {
		return new RetryExecutor(requestExecutor, new ExponentialRandomBackoffConfig(maxAttempts, new ArrayList<>())
				.createRetryConfig());
	}

}
