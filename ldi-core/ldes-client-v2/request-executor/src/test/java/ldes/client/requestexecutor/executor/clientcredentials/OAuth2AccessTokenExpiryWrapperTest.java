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

	@Test
	void getAccessToken() {

	}

}