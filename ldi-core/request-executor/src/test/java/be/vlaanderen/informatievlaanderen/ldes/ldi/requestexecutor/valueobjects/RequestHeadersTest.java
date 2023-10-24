package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RequestHeadersTest {

	@Test
	void getFirst() {
		final String contentType = "application/json";
		final List<RequestHeader> requestHeaders = List.of(new RequestHeader("content-type", contentType));
		Optional<String> firstHeader = new RequestHeaders(requestHeaders).getFirst("cOnTenT-TYPE")
				.map(RequestHeader::getValue);
		assertTrue(firstHeader.isPresent());
		assertEquals(contentType, firstHeader.get());
	}

}