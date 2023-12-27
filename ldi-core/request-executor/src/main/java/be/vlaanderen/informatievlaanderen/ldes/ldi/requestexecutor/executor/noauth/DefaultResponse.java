package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DefaultResponse {

	private final HttpResponse httpResponse;
	private final Request request;

	public DefaultResponse(Request request, HttpResponse httpResponse) {
		this.request = request;
		this.httpResponse = httpResponse;
	}

	public Response getResponse() throws IOException {
		final List<Header> headers = Arrays.stream(httpResponse.getAllHeaders()).toList();
		final int statusCode = httpResponse.getStatusLine().getStatusCode();
		final byte[] body = httpResponse.getEntity() != null ? EntityUtils.toByteArray(httpResponse.getEntity()) : null;
		return new Response(request, headers, statusCode, body);
	}

}
