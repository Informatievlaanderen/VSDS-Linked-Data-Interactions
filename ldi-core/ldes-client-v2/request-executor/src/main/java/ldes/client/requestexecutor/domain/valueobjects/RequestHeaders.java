package ldes.client.requestexecutor.domain.valueobjects;

import org.apache.http.HttpHeaders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class RequestHeaders implements Iterable<RequestHeader> {

	private final List<RequestHeader> headers;

	public RequestHeaders(List<RequestHeader> requestHeaders) {
		this.headers = requestHeaders;
	}

	public RequestHeaders addRequestHeader(RequestHeader requestHeader) {
		List<RequestHeader> newHeaders = new ArrayList<>(headers);
		newHeaders.add(requestHeader);
		return new RequestHeaders(newHeaders);
	}

	public Optional<RequestHeader> getContentType() {
		return headers.stream().filter(f -> HttpHeaders.CONTENT_TYPE.equals(f.getKey())).findFirst();
	}

	@Override
	public Iterator<RequestHeader> iterator() {
		return headers.iterator();
	}
}
