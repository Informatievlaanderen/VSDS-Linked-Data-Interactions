package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials.ClientCredentialsConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.EdcRequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects.EdcUrlProxy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.ApiKeyConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.DefaultConfig;

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

	public RequestExecutor createEdcExecutor(RequestExecutor requestExecutor,
			TokenService tokenService,
			EdcUrlProxy edcUrlProxy) {
		return new EdcRequestExecutor(requestExecutor, tokenService, edcUrlProxy);
	}

}
