package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
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
			return new DefaultResponse(request, httpClient.execute(httpRequest)).getResponse();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

}
