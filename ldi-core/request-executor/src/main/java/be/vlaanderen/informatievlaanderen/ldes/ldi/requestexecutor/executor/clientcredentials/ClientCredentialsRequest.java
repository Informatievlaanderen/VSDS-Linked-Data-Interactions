package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

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
