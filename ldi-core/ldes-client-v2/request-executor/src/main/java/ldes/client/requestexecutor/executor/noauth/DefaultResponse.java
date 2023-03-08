package ldes.client.requestexecutor.executor.noauth;

import ldes.client.requestexecutor.domain.valueobjects.Response;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultResponse {

	private final HttpResponse httpResponse;

	public DefaultResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	// TODO: 6/03/2023 test
	public Response getResponse() throws IOException {
		final Map<String, String> headers = extractResponseHeaders(httpResponse.getAllHeaders());
		final int statusCode = httpResponse.getStatusLine().getStatusCode();
		final String body = httpResponse.getEntity() != null ? EntityUtils.toString(httpResponse.getEntity()) : null;
		return new Response(headers, statusCode, body);
	}

	private Map<String, String> extractResponseHeaders(Header[] allHeaders) {
		return Stream.of(allHeaders)
				.collect(
						Collectors.toMap(Header::getName, Header::getValue, (a, b) -> a + "," + b));
	}

}
