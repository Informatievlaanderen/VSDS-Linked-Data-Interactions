package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class DefaultRequestExecutor implements RequestExecutor {

	private final HttpClient httpClient;
	private final Collection<Header> customHeaders;

	public DefaultRequestExecutor(HttpClient httpClient, Collection<Header> customHeaders) {
		this.httpClient = httpClient;
		this.customHeaders = customHeaders;
	}

	@Override
	public Response execute(Request request) {
		try {
			HttpUriRequest httpRequest = new DefaultRequest(request).getHttpRequest();
			return new DefaultResponse(request, new ArrayList<>(customHeaders), httpClient.execute(httpRequest)).getResponse();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

}
