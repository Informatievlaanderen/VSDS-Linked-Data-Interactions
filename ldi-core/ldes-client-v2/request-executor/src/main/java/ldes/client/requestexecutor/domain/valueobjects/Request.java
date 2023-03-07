package ldes.client.requestexecutor.domain.valueobjects;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;

/**
 * Contains the request details to connect to the server.
 */
public class Request {

	private final String url;
	private final RequestHeaders requestHeaders;

	public Request(String url, RequestHeaders requestHeaders) {
		this.url = url;
		this.requestHeaders = requestHeaders;
	}

	public String getUrl() {
		return url;
	}

	public RequestHeaders getRequestHeaders() {
		return requestHeaders;
	}

}
