package ldes.client.requestexecutor.domain.valueobjects;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;

/**
 * Contains the request details to connect to the server.
 */
public class Request {

	// TODO: 6/03/2023 request bouwen we nu op in executor -> kunnen er interface
	// van maken en strategy patter (factories)
	// TODO: 6/03/2023 processor geen meerwaarde? direct executor?

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
