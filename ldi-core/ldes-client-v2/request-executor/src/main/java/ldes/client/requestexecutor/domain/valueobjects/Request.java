package ldes.client.requestexecutor.domain.valueobjects;

import org.apache.http.entity.ContentType;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Request request))
			return false;
		return Objects.equals(url, request.url) && Objects.equals(requestHeaders, request.requestHeaders);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, requestHeaders);
	}

	public String getUrl() {
		return url;
	}

	public RequestHeaders getRequestHeaders() {
		return requestHeaders;
	}

	public String getContentType() {
		return requestHeaders.getContentType().map(RequestHeader::getValue).orElse(ContentType.WILDCARD.getMimeType());
	}
}
