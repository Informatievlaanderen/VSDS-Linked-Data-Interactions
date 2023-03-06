package ldes.client.requestexecutor.domain.valueobjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RequestHeaders implements Iterable<RequestHeader> {

	private final List<RequestHeader> headers;

	public RequestHeaders(List<RequestHeader> requestHeaders) {
		this.headers = requestHeaders;
	}

	public RequestHeaders addHeader(RequestHeader requestHeader) {
		List<RequestHeader> newHeaders = new ArrayList<>(headers);
		newHeaders.add(requestHeader);
		return new RequestHeaders(newHeaders);
	}

	@Override
	public Iterator<RequestHeader> iterator() {
		return headers.iterator();
	}

}
