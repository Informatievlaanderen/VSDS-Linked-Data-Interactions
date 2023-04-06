package ldes.client.requestexecutor.executor.clientcredentials;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.scribejava.core.model.OAuth2AccessToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OAuth2AccessTokenExpiryWrapperTest {

	@Nested
	class From {
		@Test
		void shouldCreateEmptyInstance_whenExpiryIsMissing() {
			OAuth2AccessTokenExpiryWrapper token = OAuth2AccessTokenExpiryWrapper.from(new OAuth2AccessToken("token"));
			assertTrue(token.getAccessToken().isEmpty());
		}

		@Test
		void shouldContainToken_whenTokenAndExpiryArePresent() {
			OAuth2AccessToken token = new OAuth2AccessToken("accessToken", "tokenType",
					3600, "refreshToken", "scope", "rawResponse");
			OAuth2AccessTokenExpiryWrapper tokenWrapper = OAuth2AccessTokenExpiryWrapper.from(token);
			assertTrue(tokenWrapper.getAccessToken().isPresent());
			assertEquals("accessToken", tokenWrapper.getAccessToken().orElseThrow().getAccessToken());
		}
	}

	@Test
	void shouldCreateEmptyToken_whenUsingEmptyCtr() {
		OAuth2AccessTokenExpiryWrapper empty = OAuth2AccessTokenExpiryWrapper.empty();
		assertTrue(empty.getAccessToken().isEmpty());
	}

	@Nested
	class GetAccessToken {

		@Test
		void shouldReturnToken_whenPresentAndExpiresInMoreThan30Seconds() {
			OAuth2AccessToken token = new OAuth2AccessToken("accessToken", "tokenType",
					3600, "refreshToken", "scope", "rawResponse");

			assertEquals(token, OAuth2AccessTokenExpiryWrapper.from(token).getAccessToken().orElseThrow());
		}

		@Test
		void shouldReturnEmpty_whenTokenNotSet() {
			assertTrue(OAuth2AccessTokenExpiryWrapper.empty().getAccessToken().isEmpty());
		}

		@Test
		void shouldReturnEmpty_whenExpiryNotSet() {
			OAuth2AccessToken token = new OAuth2AccessToken("accessToken", "tokenType",
					null, "refreshToken", "scope", "rawResponse");

			assertTrue(OAuth2AccessTokenExpiryWrapper.from(token).getAccessToken().isEmpty());
		}

		@Test
		void shouldReturnEmpty_whenExpiresInLessThan30Seconds() {
			OAuth2AccessToken token = new OAuth2AccessToken("accessToken", "tokenType",
					29, "refreshToken", "scope", "rawResponse");

			assertTrue(OAuth2AccessTokenExpiryWrapper.from(token).getAccessToken().isEmpty());
		}

	}

}