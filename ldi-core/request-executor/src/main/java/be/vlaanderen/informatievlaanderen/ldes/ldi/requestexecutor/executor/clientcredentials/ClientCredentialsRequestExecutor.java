package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
			final List<Header> headers = extractHeaders(response.getHeaders());
			return new Response(request, headers, response.getCode(), response.getBody());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new HttpRequestException(e);
		} catch (IOException | ExecutionException e) {
			throw new HttpRequestException(e);
		}
	}

	private List<Header> extractHeaders(Map<String, String> headers) {
		return headers.entrySet().stream()
				.map(entry -> new BasicHeader(entry.getKey(), entry.getValue()))
				.map(Header.class::cast).toList();
	}

}
