package ldes.client.requestexecutor.domain.valueobjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RequestHeaders that = (RequestHeaders) o;
		return Objects.equals(headers, that.headers);
	}

	@Override
	public int hashCode() {
		return Objects.hash(headers);
	}

}
