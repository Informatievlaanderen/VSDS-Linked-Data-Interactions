package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PostRequestTest {

	private final static String BODY = "body";
	private final static String URL = "url";

	@Test
	void getMethod() {
		assertThat(new PostRequest(URL, RequestHeaders.empty(), BODY).getMethod()).isEqualTo("POST");
	}

	@Test
	void getBody() {
		assertThat(new PostRequest(URL, RequestHeaders.empty(), BODY).getBodyAsString()).isEqualTo(BODY);
	}

	@Test
	void getContentType() {
		final String contentType = "application/json";
		final List<RequestHeader> requestHeaders = List.of(new RequestHeader("content-type", contentType));
		assertThat(new PostRequest(URL, new RequestHeaders(requestHeaders), BODY).getContentType())
				.isEqualTo(contentType);
	}

	@ParameterizedTest
	@ArgumentsSource(EqualityTestProvider.class)
	void testEqualsAndHashCode(BiConsumer<Object, Object> assertion, PostRequest a, PostRequest b) {
		assertThat(assertion).isNotNull();
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
					Arguments.of(notEquals(), new PostRequest(null, RequestHeaders.empty(), (String) null), requestA));
		}

		private static BiConsumer<Object, Object> equals() {
			return (a, b) -> assertThat(a).isEqualTo(b);
		}

		private static BiConsumer<Object, Object> notEquals() {
			return (a, b) -> assertThat(a).isNotEqualTo(b);
		}

	}

	@Test
	void test_WithUrl() {
		var initialRequest = new PostRequest("url", new RequestHeaders(List.of(new RequestHeader("key", "val"))),
				"body");

		var requestWithOtherUrl = initialRequest.with("other-url");

		assertThat(requestWithOtherUrl.getUrl()).isNotEqualTo(initialRequest.getUrl());
		assertThat(requestWithOtherUrl)
				.hasFieldOrPropertyWithValue("method", initialRequest.getMethod())
				.hasFieldOrPropertyWithValue("requestHeaders", initialRequest.getRequestHeaders());
	}

	@Test
	void test_WithRequestHeaders() {
		var initialRequest = new PostRequest("url", new RequestHeaders(List.of(new RequestHeader("key", "val"))),
				"body");

		var requestWithOtherHeaders = initialRequest
				.with(new RequestHeaders(List.of(new RequestHeader("other-key", "val"))));

		assertThat(requestWithOtherHeaders.getRequestHeaders()).isNotEqualTo(initialRequest.getRequestHeaders());
		assertThat(requestWithOtherHeaders)
				.hasFieldOrPropertyWithValue("method", initialRequest.getMethod())
				.hasFieldOrPropertyWithValue("url", initialRequest.getUrl());
	}

}