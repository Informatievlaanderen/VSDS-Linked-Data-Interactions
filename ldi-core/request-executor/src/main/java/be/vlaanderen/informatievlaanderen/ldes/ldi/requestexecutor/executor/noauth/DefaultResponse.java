package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Stream.concat;

public class DefaultResponse {

	private final HttpResponse httpResponse;
	private final Request request;
	private final List<Header> headers;

	public DefaultResponse(Request request, List<Header> customHeaders, HttpResponse httpResponse) {
		this.request = request;
		this.httpResponse = httpResponse;
		this.headers = customHeaders;
	}

	public Response getResponse() throws IOException {
		final List<Header> headers = concat(Arrays.stream(httpResponse.getAllHeaders()), this.headers.stream()).collect(toCollection(ArrayList::new));
		final int statusCode = httpResponse.getStatusLine().getStatusCode();
		final String body = httpResponse.getEntity() != null ? EntityUtils.toString(httpResponse.getEntity()) : null;
		return new Response(request, headers, statusCode, body);
	}

}
