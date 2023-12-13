package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials.ClientCredentialsConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.EdcRequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects.EdcUrlProxy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.ApiKeyConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.DefaultConfig;
import org.apache.http.Header;

import java.util.Collection;

public class RequestExecutorFactory {

	public RequestExecutor createApiKeyExecutor(String keyHeader, String key, Collection<Header> customHeaders) {
		return new ApiKeyConfig(keyHeader, key).createRequestExecutor(customHeaders);
	}

	public RequestExecutor createNoAuthExecutor(Collection<Header> customHeaders) {
		return new DefaultConfig().createRequestExecutor(customHeaders);
	}

	public RequestExecutor createClientCredentialsExecutor(String clientId,
														   String secret,
														   String tokenEndpoint, Collection<Header> customHeaders) {
		return new ClientCredentialsConfig(clientId, secret, tokenEndpoint).createRequestExecutor(customHeaders);
	}

	public RequestExecutor createEdcExecutor(RequestExecutor requestExecutor,
			TokenService tokenService,
			EdcUrlProxy edcUrlProxy) {
		return new EdcRequestExecutor(requestExecutor, tokenService, edcUrlProxy);
	}

}
