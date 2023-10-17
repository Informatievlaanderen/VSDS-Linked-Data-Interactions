package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class DefaultRequest {

	private final Request request;

	public DefaultRequest(Request request) {
		this.request = request;
	}

	public HttpUriRequest getHttpRequest() {
		final HttpRequestBase httpRequest = createRequest();
		request.getRequestHeaders().forEach(header -> httpRequest.addHeader(header.getKey(), header.getValue()));
		return httpRequest;
	}

	private HttpRequestBase createRequest() {
		return switch (request.getMethod()) {
			case GetRequest.METHOD_NAME -> new HttpGet(request.getUrl());
			case PostRequest.METHOD_NAME -> {
				final HttpPost post = new HttpPost(request.getUrl());
				final PostRequest postRequest = (PostRequest) request;
				final ContentType contentType = ContentType.create(postRequest.getContentType());
				post.setEntity(new StringEntity(postRequest.getBody(), contentType));
				yield post;
			}
			default -> throw new IllegalStateException("Http method not supported: " + request.getMethod());
		};
	}

}
