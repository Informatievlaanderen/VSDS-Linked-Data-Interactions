package ldes.client.requestexecutor.domain.valueobjects;

import java.util.ArrayList;
import java.util.List;

public class RequestHeaders {
	private final List<RequestHeader> headers;

	public RequestHeaders(List<RequestHeader> requestHeaders) {
		this.headers = requestHeaders;
	}

	public RequestHeaders addRequestHeader(RequestHeader requestHeader) {
		List<RequestHeader> newHeaders = new ArrayList<>(headers);
		newHeaders.add(requestHeader);
		return new RequestHeaders(newHeaders);
	}

	public List<RequestHeader> getHeaders() {
		return headers;
	}
}
