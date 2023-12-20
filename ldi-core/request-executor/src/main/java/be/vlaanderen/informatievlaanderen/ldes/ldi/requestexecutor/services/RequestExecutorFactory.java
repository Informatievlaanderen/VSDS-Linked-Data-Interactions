package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials.ClientCredentialsConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.EdcRequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects.EdcUrlProxy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.DefaultConfig;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Collection;

public class RequestExecutorFactory {

    public RequestExecutor createNoAuthExecutor(Collection<Header> headers) {
        return new DefaultConfig(headers).createRequestExecutor();
    }

    public RequestExecutor createNoAuthExecutor() {
        return createNoAuthExecutor(new ArrayList<>());
    }

    public RequestExecutor createClientCredentialsExecutor(Collection<Header> headers,
                                                           String clientId,
                                                           String secret,
                                                           String tokenEndpoint) {
        return new ClientCredentialsConfig(headers, clientId, secret, tokenEndpoint).createRequestExecutor();
    }

    public RequestExecutor createClientCredentialsExecutor(String clientId,
                                                           String secret,
                                                           String tokenEndpoint) {
        return createClientCredentialsExecutor(new ArrayList<>(), clientId, secret, tokenEndpoint);
    }

    public RequestExecutor createEdcExecutor(RequestExecutor requestExecutor,
                                             TokenService tokenService,
                                             EdcUrlProxy edcUrlProxy) {
        return new EdcRequestExecutor(requestExecutor, tokenService, edcUrlProxy);
    }

}
