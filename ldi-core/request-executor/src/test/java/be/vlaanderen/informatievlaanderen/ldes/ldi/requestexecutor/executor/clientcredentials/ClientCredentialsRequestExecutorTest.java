package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import com.github.scribejava.core.model.OAuth2AccessToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientCredentialsRequestExecutorTest {

	@InjectMocks
	private ClientCredentialsRequestExecutor clientCredentialsRequestExecutor;

	@Mock
	private OAuth20ServiceTokenCacheWrapper oAuthService;

	@Nested
	class Apply {
		@Test
		void shouldReturnFilledResponse_whenSuccess() throws Exception {
			OAuth2AccessToken token = new OAuth2AccessToken("accessToken", "tokenType",
					3600, "refreshToken", "scope", "rawResponse");
			when(oAuthService.getAccessTokenClientCredentialsGrant()).thenReturn(token);

			Request request = new GetRequest("url", RequestHeaders.empty());

			com.github.scribejava.core.model.Response scribeResponse = new com.github.scribejava.core.model.Response(
					200, "OK", Map.of("key", "value"), "body");
			when(oAuthService.execute(any())).thenReturn(scribeResponse);

			Response response = clientCredentialsRequestExecutor.execute(request);

			verify(oAuthService).signRequest(any(), any());
			assertThat(response.getHttpStatus()).isEqualTo(scribeResponse.getCode());
			assertThat(response.getBodyAsString()).contains(scribeResponse.getBody());
			assertThat(response.getFirstHeaderValue("key")).contains(scribeResponse.getHeader("key"));
		}

		@Test
		void shouldThrowHtppException_whenIOException() throws Exception {
			Request request = new GetRequest("url", RequestHeaders.empty());
			when(oAuthService.execute(any())).thenThrow(IOException.class);

			assertThatThrownBy(() -> clientCredentialsRequestExecutor.execute(request))
					.isInstanceOf(HttpRequestException.class)
					.hasCauseInstanceOf(IOException.class);
		}

		@Test
		void shouldThrowHtppException_whenInterrupted() throws Exception {
			Request request = new GetRequest("url", RequestHeaders.empty());

			when(oAuthService.execute(any())).thenThrow(InterruptedException.class);

			assertThatThrownBy(() ->clientCredentialsRequestExecutor.execute(request))
					.isInstanceOf(HttpRequestException.class)
					.hasCauseInstanceOf(InterruptedException.class);
		}

		@Test
		void shouldNotThrowHttpException_whenInterrupted() throws Exception {
			Request request = new GetRequest("url", RequestHeaders.empty());

			when(oAuthService.execute(any())).thenThrow(NullPointerException.class);

			assertThatThrownBy(() -> clientCredentialsRequestExecutor.execute(request))
					.isInstanceOf(NullPointerException.class);
		}

	}
}
