package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Request request = (Request) o;
		return Objects.equals(url, request.url) && Objects.equals(requestHeaders, request.requestHeaders);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, requestHeaders);
	}

}
