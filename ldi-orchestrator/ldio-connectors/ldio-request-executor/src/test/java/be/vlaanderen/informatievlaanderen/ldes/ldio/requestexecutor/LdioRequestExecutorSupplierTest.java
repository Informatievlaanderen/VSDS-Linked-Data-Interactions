package be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.RequestExecutorProperties.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LdioRequestExecutorSupplierTest {

	@Mock
	private RequestExecutorFactory requestExecutorFactory;

	@InjectMocks
	private LdioRequestExecutorSupplier requestExecutorSupplier;

	@Test
	void shouldReturnRetryExecutorWithDefaults_whenNoProperties() {
		ComponentProperties properties = new ComponentProperties(Map.of());
		RequestExecutor requestExecutor = mock(RequestExecutor.class);
		when(requestExecutorFactory.createNoAuthExecutor()).thenReturn(requestExecutor);
		RequestExecutor retryRequestExecutor = mock(RequestExecutor.class);
//		when(requestExecutorFactory.createRetryExecutor(requestExecutor, 5, List.of()))
//				.thenReturn(retryRequestExecutor);

		RequestExecutor result = requestExecutorSupplier.getRequestExecutor(properties);

		assertEquals(retryRequestExecutor, result);
	}

	@Test
	void shouldReturnRetryExecutorWithConfiguredProperties_whenPropertiesConfigured() {
		String maxRetries = "10";
		ComponentProperties properties = new ComponentProperties(Map.of(
				MAX_RETRIES, maxRetries,
				STATUSES_TO_RETRY, "400,404",
				AUTH_TYPE, AuthStrategy.API_KEY.name(),
				API_KEY_HEADER, "key-header",
				API_KEY, "key"));
		RequestExecutor requestExecutor = mock(RequestExecutor.class);
		when(requestExecutorFactory.createApiKeyExecutor("key-header", "key")).thenReturn(requestExecutor);
		RequestExecutor retryRequestExecutor = mock(RequestExecutor.class);
//		when(requestExecutorFactory.createRetryExecutor(requestExecutor, Integer.parseInt(maxRetries),
//				List.of(400, 404)))
//				.thenReturn(retryRequestExecutor);

		RequestExecutor result = requestExecutorSupplier.getRequestExecutor(properties);

		assertEquals(retryRequestExecutor, result);
	}

	@Test
	void shouldReturnNonRetryExecutorWithConfiguredProperties_whenPropertiesConfigured() {
		ComponentProperties properties = new ComponentProperties(Map.of(
				RETRIES_ENABLED, "false",
				AUTH_TYPE, AuthStrategy.OAUTH2_CLIENT_CREDENTIALS.name(),
				CLIENT_ID, "client",
				CLIENT_SECRET, "secret",
				TOKEN_ENDPOINT, "token"));
		RequestExecutor requestExecutor = mock(RequestExecutor.class);
		when(requestExecutorFactory.createClientCredentialsExecutor("client", "secret", "token"))
				.thenReturn(requestExecutor);

		RequestExecutor result = requestExecutorSupplier.getRequestExecutor(properties);

		assertEquals(requestExecutor, result);
	}

	@Test
	void shouldThrowException_whenAuthTypeNotSupported() {
		ComponentProperties properties = new ComponentProperties(Map.of(AUTH_TYPE, "fantasy"));
		assertThrows(UnsupportedOperationException.class,
				() -> requestExecutorSupplier.getRequestExecutor(properties));
	}
}