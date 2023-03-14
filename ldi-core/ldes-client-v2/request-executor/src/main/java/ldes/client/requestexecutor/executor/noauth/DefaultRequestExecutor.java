package ldes.client.requestexecutor.executor.noauth;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.exceptions.HttpRequestException;
import ldes.client.requestexecutor.executor.RequestExecutor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class DefaultRequestExecutor implements RequestExecutor {

	private final HttpClient httpClient;

	public DefaultRequestExecutor(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public Response execute(Request request) {
		try {
			HttpUriRequest httpRequest = new DefaultRequest(request).getHttpRequest();
			return new DefaultResponse(httpClient.execute(httpRequest)).getResponse();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

}
