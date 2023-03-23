package ldes.client.requestexecutor.executor.noauth;

import ldes.client.requestexecutor.domain.valueobjects.Response;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultResponse {

	private final HttpResponse httpResponse;

	public DefaultResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	public Response getResponse() throws IOException {
		final List<Header> headers = Arrays.stream(httpResponse.getAllHeaders()).toList();
		final int statusCode = httpResponse.getStatusLine().getStatusCode();
		final String body = httpResponse.getEntity() != null ? EntityUtils.toString(httpResponse.getEntity()) : null;
		return new Response(headers, statusCode, body);
	}

}
