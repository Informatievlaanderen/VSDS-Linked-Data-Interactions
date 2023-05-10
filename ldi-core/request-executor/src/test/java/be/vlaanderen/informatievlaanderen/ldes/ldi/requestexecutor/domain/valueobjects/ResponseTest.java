package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

	@Nested
	class TestGetRedirectLocation {
		@Test
		void shouldReturnEmpty_whenNoLocationHeader() {
			Request request = new Request("https://example.com", RequestHeaders.empty());
			Response response = new Response(request, List.of(), 302, null);

			assertTrue(response.getRedirectLocation().isEmpty());
		}

		@Test
		void shouldReturnLocationUrl_whenAbsolute() {
			Request request = new Request("https://example.com/blog/article", RequestHeaders.empty());
			Header header = new BasicHeader(HttpHeaders.LOCATION, "https://example.org/blog/chat");
			Response response = new Response(request, List.of(header), 302, null);

			assertEquals("https://example.org/blog/chat", response.getRedirectLocation().orElseThrow());
		}

		@Test
		void shouldAddBaseUrlToLocationUrl_whenRelativeAbsolute() {
			Request request = new Request("https://example.com/blog/article", RequestHeaders.empty());
			Header header = new BasicHeader(HttpHeaders.LOCATION, "/chat");
			Response response = new Response(request, List.of(header), 302, null);

			assertEquals("https://example.com/chat", response.getRedirectLocation().orElseThrow());
		}

		@Test
		void shouldAddUrlToLocationUrl_whenRelativeRelative() {
			Request request = new Request("https://example.com/blog/article", RequestHeaders.empty());
			Header header = new BasicHeader(HttpHeaders.LOCATION, "chat");
			Response response = new Response(request, List.of(header), 302, null);

			assertEquals("https://example.com/blog/chat", response.getRedirectLocation().orElseThrow());
		}
	}

}