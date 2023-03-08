package ldes.client.requestexecutor.executor.clientcredentials;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.RequestExecutor;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;

/**
 * Handles sending the actual HTTP request to the server.
 */
public class ClientCredentialsRequestExecutor implements RequestExecutor {

	private final OAuth20ServiceTokenCacheWrapper oAuthService;

	public ClientCredentialsRequestExecutor(OAuth20ServiceTokenCacheWrapper oAuthService) {
		this.oAuthService = oAuthService;
	}

	@Override
	public Response execute(Request request) {
		final OAuth2AccessToken token = oAuthService.getAccessTokenClientCredentialsGrant();
		final OAuthRequest oAuthRequest = new ClientCredentialsRequest(request).getOAuthRequest();
		oAuthService.signRequest(token, oAuthRequest);

		try (com.github.scribejava.core.model.Response response = oAuthService.execute(oAuthRequest)) {
			return new Response(response.getHeaders(), response.getCode(), response.getBody());
		} catch (IOException | InterruptedException | ExecutionException e) {
			// TODO: 6/03/2023 handle exceptions properly
			throw new RuntimeException(e);
		}
	}

}
