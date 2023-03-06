package ldes.client.requestexecutor.executor.noauth;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

public class DefaultRequest {

	private final Request request;

	public DefaultRequest(Request request) {
		this.request = request;
	}

	// TODO: 6/03/2023 test
	public HttpUriRequest getHttpRequest() {
		final HttpGet httpRequest = new HttpGet(request.getUrl());
		request.getRequestHeaders().forEach(header -> httpRequest.addHeader(header.getKey(), header.getValue()));
		return httpRequest;
	}

}
