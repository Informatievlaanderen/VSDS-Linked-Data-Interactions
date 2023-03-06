package ldes.client.requestexecutor.executor.clientcredentials;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.RequestExecutor;
import org.apache.http.HttpHeaders;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;

/**
 * Handles sending the actual HTTP request to the server.
 */
public class ClientCredentialsRequestExecutor implements RequestExecutor {

    private final OAuth20ServiceTokenCacheWrapper oAuthService;

    public ClientCredentialsRequestExecutor(OAuth20ServiceTokenCacheWrapper oAuthService) {
        this.oAuthService = oAuthService;
    }

    // TODO: 3/03/2023 discuss with Wouter: Auth scope op client of request?
    // TODO: 6/03/2023 code gaat enkel zonder retry

    @Override
    public Response apply(Request request) {
        final OAuth2AccessToken token = oAuthService.getAccessTokenClientCredentialsGrant();
        final OAuthRequest oAuthRequest = toOAuthRequest(token, request);

        try (com.github.scribejava.core.model.Response response = oAuthService.execute(oAuthRequest)) {
            return new Response(response.getHeaders(), response.getCode(), response.getBody());
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public OAuthRequest toOAuthRequest(OAuth2AccessToken token, Request request) {
        final OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, request.getUrl());
        oAuthService.signRequest(token, oAuthRequest);
        request.getRequestHeaders().forEach(header -> oAuthRequest.addHeader(header.getKey(), header.getValue()));
        return oAuthRequest;
    }


}
