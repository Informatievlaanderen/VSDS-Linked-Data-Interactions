package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.HeadersMatcher;
import org.apache.http.Header;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeadersTest {
	private static final String HEADER_KEY = "header";
	@Mock
	private ApplicationArguments arguments;
	@InjectMocks
	private Headers headers;

	@Test
	void given_OneHeaderProvided_when_GetHeaders_then_ReturnSingletonList() {
		final Collection<HeadersMatcher.CustomHeader> expectedHeaders = List.of(new HeadersMatcher.CustomHeader("Content-Type", "application/json"));
		when(arguments.getOptionValues(HEADER_KEY)).thenReturn(List.of("Content-Type: application/json"));

		final List<Header> actual = headers.getHeaders();

		assertThat(actual)
				.map(HeadersMatcher.CustomHeader::fromHeader)
				.containsExactlyElementsOf(expectedHeaders);
	}

	@Test
	void given_TwoHeadersProvided_when_GetHeaders_then_ReturnListOfTwoHeaders() {
		final Collection<HeadersMatcher.CustomHeader> expectedHeaders = List.of(
				new HeadersMatcher.CustomHeader("Content-Type", "application/json"),
				new HeadersMatcher.CustomHeader("Accept", "application/rdf+protobuf")
		);
		when(arguments.getOptionValues(HEADER_KEY)).thenReturn(List.of("Content-Type: application/json", "Accept: application/rdf+protobuf"));

		final List<Header> actual = headers.getHeaders();

		assertThat(actual)
				.map(HeadersMatcher.CustomHeader::fromHeader)
				.containsExactlyElementsOf(expectedHeaders);
	}

	@Test
	void given_NullHeaders_when_GetHeaders_then_ReturnEmptyList() {
		final List<Header> actual = headers.getHeaders();

		assertThat(actual).isEmpty();
	}

	@Test
	void given_InvalidHeaders_when_GetHeaders_then_ReturnEmptyList() {
		when(arguments.getOptionValues(HEADER_KEY))
				.thenReturn(List.of("", "Content-Type=text/turtle", ":", "Header-Key:", ":Header-Value"));

		final List<Header> actual = headers.getHeaders();

		assertThat(actual).isEmpty();
	}
}