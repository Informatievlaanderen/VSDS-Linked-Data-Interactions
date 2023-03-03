package ldes.client.requestexecutor.domain.valueobjects;

import java.net.URI;
import java.net.http.HttpRequest;
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

	public HttpRequest createHttpRequest() {
		HttpRequest.Builder uri = HttpRequest.newBuilder()
				.uri(URI.create(url));
		for (RequestHeader requestHeader : requestHeaders.getHeaders()) {
			uri = uri.header(requestHeader.getKey(), requestHeader.getValue());
		}
		return uri
				.GET()
				.build();
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
}
