package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Contains the request details to connect to the server.
 */
public abstract class Request {

	final String url;
	final RequestHeaders requestHeaders;

	protected Request(String url, RequestHeaders requestHeaders) {
		this.url = url;
		this.requestHeaders = notNull(requestHeaders);
	}

	public String getUrl() {
		return url;
	}

	public RequestHeaders getRequestHeaders() {
		return requestHeaders;
	}

	public abstract String getMethod();

	public abstract Request with(String url);

	public abstract Request with(RequestHeaders requestHeaders);

}
