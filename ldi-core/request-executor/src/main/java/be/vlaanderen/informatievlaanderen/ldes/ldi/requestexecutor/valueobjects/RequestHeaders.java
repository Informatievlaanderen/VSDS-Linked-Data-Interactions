package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.lowerCase;

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

	// TODO TVB: 17/10/23 test
	public Optional<RequestHeader> getFirst(String key) {
		return headers.stream()
				.filter(header -> Objects.equals(lowerCase(header.getKey()), lowerCase(key)))
				.findFirst();
	}
}
