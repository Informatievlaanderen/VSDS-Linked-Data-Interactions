package ldes.client.requestexecutor.domain.valueobjects;

import org.apache.http.HttpHeaders;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Objects;

/**
 * Contains the request details to connect to the server.
 */
public class Request {

	private final String url;
	private final String contentType;

	public Request(String url, String contentType) {
		this.url = url;
		this.contentType = contentType;
	}

	public HttpRequest createHttpRequest()  {
		return HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header(HttpHeaders.ACCEPT, contentType)
				.GET()
				.build();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Request request)) return false;
		return Objects.equals(url, request.url) && Objects.equals(contentType, request.contentType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, contentType);
	}
}
