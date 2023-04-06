package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RequestHeaders implements Iterable<RequestHeader> {

	private final List<RequestHeader> headers;

	public RequestHeaders(List<RequestHeader> requestHeaders) {
		this.headers = requestHeaders;
	}

	public RequestHeaders addRequestHeader(RequestHeader requestHeader) {
		ArrayList<RequestHeader> updatedRequestHeaders = new ArrayList<>(headers);
		updatedRequestHeaders.add(requestHeader);
		return new RequestHeaders(updatedRequestHeaders);
	}

	public static RequestHeaders empty() {
		return new RequestHeaders(new ArrayList<>());
	}

	@Override
	public Iterator<RequestHeader> iterator() {
		return headers.iterator();
	}

}
