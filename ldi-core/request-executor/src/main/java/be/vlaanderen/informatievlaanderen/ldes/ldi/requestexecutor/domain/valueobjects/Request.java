package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Contains the request details to connect to the server.
 */
public class Request {

	private final String url;
	private final RequestHeaders requestHeaders;

	public Request(String url, RequestHeaders requestHeaders) {
		this.url = url;
		this.requestHeaders = notNull(requestHeaders);
	}

	public String getUrl() {
		return url;
	}

	public RequestHeaders getRequestHeaders() {
		return requestHeaders;
	}

}
