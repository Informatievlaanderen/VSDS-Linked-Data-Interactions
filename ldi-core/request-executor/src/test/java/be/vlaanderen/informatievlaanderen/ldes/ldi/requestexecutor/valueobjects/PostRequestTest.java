package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PostRequestTest {

	private final static String BODY = "body";
	private final static String URL = "url";

	@Test
	void getMethod() {
		assertEquals("POST", new PostRequest(URL, RequestHeaders.empty(), BODY).getMethod());
	}

	@Test
	void getBody() {
		assertEquals(BODY, new PostRequest(URL, RequestHeaders.empty(), BODY).getBody());
	}

	@Test
	void getContentType() {
		final String contentType = "application/json";
		final List<RequestHeader> requestHeaders = List.of(new RequestHeader("content-type", contentType));
		assertEquals(contentType, new PostRequest(URL, new RequestHeaders(requestHeaders), BODY).getContentType());
	}

	@ParameterizedTest
	@ArgumentsSource(EqualityTestProvider.class)
	void testEqualsAndHashCode(BiConsumer<Object, Object> assertion, PostRequest a, PostRequest b) {
		assertNotNull(assertion);
		assertion.accept(a, b);
		if (a != null && b != null) {
			assertion.accept(a.hashCode(), b.hashCode());
		}
	}

	static class EqualityTestProvider implements ArgumentsProvider {

		private static final PostRequest requestA = new PostRequest("url",
				new RequestHeaders(List.of(new RequestHeader("key", "val"))), "body");

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(equals(), requestA, requestA),
					Arguments.of(equals(),
							new PostRequest("url", new RequestHeaders(List.of(new RequestHeader("key", "val"))),
									"body"),
							requestA),
					Arguments.of(notEquals(),
							new PostRequest("other-url", new RequestHeaders(List.of(new RequestHeader("key", "val"))),
									"body"),
							requestA),
					Arguments.of(notEquals(),
							new PostRequest("url", new RequestHeaders(List.of(new RequestHeader("other-key", "val"))),
									"body"),
							requestA),
					Arguments.of(notEquals(),
							new PostRequest("url", new RequestHeaders(List.of(new RequestHeader("key", "other-val"))),
									"body"),
							requestA),
					Arguments.of(notEquals(),
							new PostRequest("url", new RequestHeaders(List.of(new RequestHeader("key", "val"))),
									"other-body"),
							requestA),
					Arguments.of(notEquals(), new PostRequest(null, RequestHeaders.empty(), null), requestA));
		}

		private static BiConsumer<Object, Object> equals() {
			return Assertions::assertEquals;
		}

		private static BiConsumer<Object, Object> notEquals() {
			return Assertions::assertNotEquals;
		}

	}

	@Test
	void test_WithUrl() {
		var initialRequest = new PostRequest("url", new RequestHeaders(List.of(new RequestHeader("key", "val"))),
				"body");

		var requestWithOtherUrl = initialRequest.with("other-url");
		assertEquals(initialRequest.getRequestHeaders(), requestWithOtherUrl.getRequestHeaders());
		assertEquals(initialRequest.getMethod(), requestWithOtherUrl.getMethod());
		assertNotEquals(initialRequest.getUrl(), requestWithOtherUrl.getUrl());
	}

	@Test
	void test_WithRequestHeaders() {
		var initialRequest = new PostRequest("url", new RequestHeaders(List.of(new RequestHeader("key", "val"))),
				"body");

		var requestWithOtherUrl = initialRequest
				.with(new RequestHeaders(List.of(new RequestHeader("other-key", "val"))));
		assertNotEquals(initialRequest.getRequestHeaders(), requestWithOtherUrl.getRequestHeaders());
		assertEquals(initialRequest.getMethod(), requestWithOtherUrl.getMethod());
		assertEquals(initialRequest.getUrl(), requestWithOtherUrl.getUrl());
	}

}