package ldes.client.requestexecutor.executor.clientcredentials;

import ldes.client.requestexecutor.exceptions.HttpRequestException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth20ServiceTokenCacheWrapperTest {

	@Mock
	private OAuth20Service oAuth20Service;

	@InjectMocks
	private OAuth20ServiceTokenCacheWrapper wrapper;

	@Nested
	class GetAccessTokenClientCredentialsGrant {

		@Test
		void shouldGetATokenFromTheServer_whenNotCached_elseGetCachedToken() throws Exception {
			OAuth2AccessToken token = new OAuth2AccessToken("accessToken", "tokenType",
					3600, "refreshToken", "scope", "rawResponse");
			when(oAuth20Service.getAccessTokenClientCredentialsGrant()).thenReturn(token);

			assertEquals(token, wrapper.getAccessTokenClientCredentialsGrant());
			assertEquals(token, wrapper.getAccessTokenClientCredentialsGrant());

			verify(oAuth20Service, times(1)).getAccessTokenClientCredentialsGrant();
		}

		@Test
		void shouldThrowHtppException_whenIOException() throws Exception {
			when(oAuth20Service.getAccessTokenClientCredentialsGrant()).thenThrow(IOException.class);
			assertThrows(HttpRequestException.class, () -> wrapper.getAccessTokenClientCredentialsGrant());

		}

		@Test
		void shouldThrowHtppException_whenInterrupted() throws Exception {
			when(oAuth20Service.getAccessTokenClientCredentialsGrant()).thenThrow(InterruptedException.class);

			assertThrows(HttpRequestException.class, () -> wrapper.getAccessTokenClientCredentialsGrant());
		}

		@Test
		void shouldNotThrowHtppException_whenInterrupted() throws Exception {
			when(oAuth20Service.getAccessTokenClientCredentialsGrant()).thenThrow(NullPointerException.class);

			assertThrows(NullPointerException.class, () -> wrapper.getAccessTokenClientCredentialsGrant());
		}
	}

	@Test
	void whenSignRequestIsCalled_shouldPassThroughTheRequest() {
		wrapper.signRequest(any(), any());
		verify(oAuth20Service).signRequest((OAuth2AccessToken) any(), any());
	}

	@SuppressWarnings("resource")
	@Test
	void whenExecuteIsCalled_shouldPassThroughTheRequest() throws Exception {
		wrapper.execute(any());
		verify(oAuth20Service).execute(any());
	}

}