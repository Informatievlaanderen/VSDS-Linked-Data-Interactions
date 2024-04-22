package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.mockito.ArgumentMatcher;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

	public static class CustomHeader extends BasicHeader {

		public CustomHeader(String name, String value) {
			super(name, value);
		}

		public static CustomHeader fromHeader(Header header) {
			return new CustomHeader(header.getName(), header.getValue());
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Header header)) return false;
			return Objects.equals(getName(), header.getName()) && Objects.equals(getValue(), header.getValue());
		}

		@Override
		public int hashCode() {
			return Objects.hash(getName(), getValue());
		}
	}
}
