package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import org.apache.http.HttpHeaders;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;

/**
 * Wrapper around the effective own custom Request object to convert it to an OAuthRequest
 * in the effective HttpClient
 */
public class ClientCredentialsRequest {

	private final Request request;

	public ClientCredentialsRequest(Request request) {
		this.request = request;
	}

	public OAuthRequest getOAuthRequest() {
		final OAuthRequest oAuthRequest = createRequest();
		request.getRequestHeaders().forEach(header -> oAuthRequest.addHeader(header.getKey(), header.getValue()));
		return oAuthRequest;
	}

	private OAuthRequest createRequest() {
		return switch (request.getMethod()) {
			case GetRequest.METHOD_NAME -> new OAuthRequest(Verb.GET, request.getUrl());
			case PostRequest.METHOD_NAME -> {
				OAuthRequest oAuthRequest = new OAuthRequest(Verb.POST, request.getUrl());
				final PostRequest postRequest = (PostRequest) request;
				oAuthRequest.setPayload(postRequest.getBody());
				oAuthRequest.addHeader(HttpHeaders.CONTENT_TYPE, postRequest.getContentType());
				yield oAuthRequest;
			}
			default -> throw new IllegalStateException("Http method not supported: " + request.getMethod());
		};
	}

}
