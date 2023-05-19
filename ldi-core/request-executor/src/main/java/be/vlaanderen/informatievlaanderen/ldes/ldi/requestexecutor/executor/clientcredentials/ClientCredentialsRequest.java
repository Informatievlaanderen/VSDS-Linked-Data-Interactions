package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;

public class ClientCredentialsRequest {

	private final Request request;

	public ClientCredentialsRequest(Request request) {
		this.request = request;
	}

	public OAuthRequest getOAuthRequest() {
		final OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, request.getUrl());
		request.getRequestHeaders().forEach(header -> oAuthRequest.addHeader(header.getKey(), header.getValue()));
		return oAuthRequest;
	}

}
