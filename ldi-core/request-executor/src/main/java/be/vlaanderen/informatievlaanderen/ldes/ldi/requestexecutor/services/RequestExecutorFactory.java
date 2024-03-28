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

    private final boolean enableRedirectHandling;

    public RequestExecutorFactory() {
        this(true);
    }

    public RequestExecutorFactory(boolean enableRedirectHandling) {
        this.enableRedirectHandling = enableRedirectHandling;
    }

    public RequestExecutor createNoAuthExecutor(Collection<Header> headers) {
        return new DefaultConfig(headers, enableRedirectHandling).createRequestExecutor();
    }

    public RequestExecutor createNoAuthExecutor() {
        return createNoAuthExecutor(new ArrayList<>());
    }

    public RequestExecutor createClientCredentialsExecutor(Collection<Header> headers,
                                                           String clientId,
                                                           String secret,
                                                           String tokenEndpoint,
                                                           String scope) {
        var config = new ClientCredentialsConfig(headers, clientId, secret, tokenEndpoint, scope, enableRedirectHandling);
        return config.createRequestExecutor();
    }

    public RequestExecutor createClientCredentialsExecutor(String clientId,
                                                           String secret,
                                                           String tokenEndpoint,
                                                           String scope) {
        return createClientCredentialsExecutor(new ArrayList<>(), clientId, secret, tokenEndpoint, scope);
    }

    public RequestExecutor createEdcExecutor(RequestExecutor requestExecutor,
                                             TokenService tokenService,
                                             EdcUrlProxy edcUrlProxy) {
        return new EdcRequestExecutor(requestExecutor, tokenService, edcUrlProxy);
    }

}
