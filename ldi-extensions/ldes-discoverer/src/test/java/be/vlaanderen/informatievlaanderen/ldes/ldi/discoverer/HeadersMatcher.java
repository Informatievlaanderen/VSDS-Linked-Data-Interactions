package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer;

import org.apache.http.Header;
import org.mockito.ArgumentMatcher;

import java.util.Collection;
import java.util.List;

public class HeadersMatcher implements ArgumentMatcher<List<Header>> {
	private final Collection<CustomHeader> expectedHeaders;

	public HeadersMatcher(Collection<Header> expectedHeaders) {
		this.expectedHeaders = expectedHeaders.stream()
				.map(header -> new CustomHeader(header.getName(), header.getValue()))
				.toList();
	}

	public static HeadersMatcher containsAllHeaders(Collection<Header> elements) {
		return new HeadersMatcher(elements);
	}

	@Override
	public boolean matches(List<Header> headers) {
		final var actualHeaders = headers.stream().map(header -> new CustomHeader(header.getName(), header.getValue())).toList();
		return expectedHeaders.containsAll(actualHeaders);
	}

	record CustomHeader(String name, String value) {}
}
