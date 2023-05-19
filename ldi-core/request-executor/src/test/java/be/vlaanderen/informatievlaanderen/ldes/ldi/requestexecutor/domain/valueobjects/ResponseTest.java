package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseTest {

	@Test
	void getValueOfHeader() {
		Response response = new Response(null, List.of(new BasicHeader("location", "value")), 302, null);
		assertEquals("value", response.getFirstHeaderValue("LOCATION").orElseThrow());
		assertEquals("value", response.getFirstHeaderValue("lOcAtIon").orElseThrow());
		assertEquals("value", response.getFirstHeaderValue("location").orElseThrow());
	}

	@Test
	void test_isOk() {
		assertFalse(new Response(null, List.of(), HttpStatus.SC_MOVED_TEMPORARILY, null).isOk());
		assertTrue(new Response(null, List.of(), HttpStatus.SC_OK, null).isOk());
	}

	@Test
	void test_isRedirect() {
		assertTrue(new Response(null, List.of(), HttpStatus.SC_MOVED_PERMANENTLY, null).isRedirect());
		assertTrue(new Response(null, List.of(), HttpStatus.SC_MOVED_TEMPORARILY, null).isRedirect());
		assertTrue(new Response(null, List.of(), HttpStatus.SC_TEMPORARY_REDIRECT, null).isRedirect());
		assertTrue(new Response(null, List.of(), 308, null).isRedirect());
		assertFalse(new Response(null, List.of(), HttpStatus.SC_OK, null).isRedirect());
		assertFalse(new Response(null, List.of(), HttpStatus.SC_NOT_MODIFIED, null).isRedirect());
	}

	@Test
	void test_isNotModified() {
		assertTrue(new Response(null, List.of(), HttpStatus.SC_NOT_MODIFIED, null).isNotModified());
		assertFalse(new Response(null, List.of(), HttpStatus.SC_MOVED_TEMPORARILY, null).isNotModified());
		assertFalse(new Response(null, List.of(), HttpStatus.SC_OK, null).isNotModified());
	}

	@Test
	void test_hasStatus() {
		assertTrue(new Response(null, List.of(), 500, null).hasStatus(List.of(500)));
		assertTrue(new Response(null, List.of(), 500, null).hasStatus(List.of(400, 500)));
		assertFalse(new Response(null, List.of(), 500, null).hasStatus(List.of(200, 204)));
	}

	@Nested
	class TestGetRedirectLocation {
		@Test
		void shouldReturnEmpty_whenNoLocationHeader() {
			Request request = new Request("https://example.com", RequestHeaders.empty());
			Response response = new Response(request, List.of(), 302, null);

			assertTrue(response.getRedirectLocation().isEmpty());
		}

		@ParameterizedTest
		@ArgumentsSource(LocationProvider.class)
		void foo(String testName, String location) {
			assertNotNull(testName);
			Request request = new Request("https://example.com/blog/article", RequestHeaders.empty());
			Header header = new BasicHeader(HttpHeaders.LOCATION, location);
			Response response = new Response(request, List.of(header), 302, null);

			assertEquals("https://example.com/blog/chat", response.getRedirectLocation().orElseThrow());
		}

		static class LocationProvider implements ArgumentsProvider {
			@Override
			public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
				return Stream.of(
						Arguments.of("shouldReturnLocationUrl_whenAbsolute", "https://example.com/blog/chat"),
						Arguments.of("shouldAddBaseUrlToLocationUrl_whenRelativeAbsolute", "/blog/chat"),
						Arguments.of("shouldAddUrlToLocationUrl_whenRelativeRelative", "chat"));
			}
		}
	}

}