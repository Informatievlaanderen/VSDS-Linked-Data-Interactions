package ldes.client.requestexecutor.domain.valueobjects;

import java.util.Iterator;
import java.util.List;

public class RequestHeaders implements Iterable<RequestHeader> {

	private final List<RequestHeader> headers;

	public RequestHeaders(List<RequestHeader> requestHeaders) {
		this.headers = requestHeaders;
	}

	@Override
	public Iterator<RequestHeader> iterator() {
		return headers.iterator();
	}

}
