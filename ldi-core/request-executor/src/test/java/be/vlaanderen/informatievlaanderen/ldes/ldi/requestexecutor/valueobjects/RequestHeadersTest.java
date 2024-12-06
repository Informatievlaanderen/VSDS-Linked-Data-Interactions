package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestHeadersTest {

	private static final String CONTENT_TYPE = "application/json";

	@Test
	void getFirst() {
		final List<RequestHeader> requestHeaders = List.of(new RequestHeader("content-type", CONTENT_TYPE));
		Optional<String> firstHeader = new RequestHeaders(requestHeaders).getFirst("cOnTenT-TYPE")
				.map(RequestHeader::getValue);
		assertTrue(firstHeader.isPresent());
		assertEquals(CONTENT_TYPE, firstHeader.get());
	}

	@Test
	void test_Of() {
		final RequestHeaders actual = RequestHeaders.of(new RequestHeader("content-type", CONTENT_TYPE), new RequestHeader("User-Agent", "Ldio:LdesClient"));

		assertThat(actual).containsExactlyInAnyOrder(
				new RequestHeader("content-type", CONTENT_TYPE),
				new RequestHeader("User-Agent", "Ldio:LdesClient")
		);
	}

	@Test
	void test_WithRequestHeader() {
		final RequestHeaders start = RequestHeaders.of(new RequestHeader("content-type", CONTENT_TYPE));

		assertThat(start).hasSize(1);

		final RequestHeaders result = start.withRequestHeader(new RequestHeader("User-Agent", "Ldio:LdesClient"));

		assertThat(result).containsExactlyInAnyOrder(
				new RequestHeader("content-type", CONTENT_TYPE),
				new RequestHeader("User-Agent", "Ldio:LdesClient")
		);
	}
}