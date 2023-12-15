package be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorDecorator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.RequestExecutorProperties.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LdioRequestExecutorSupplierTest {

	@Mock
	private RequestExecutorFactory requestExecutorFactory;

	@InjectMocks
	private LdioRequestExecutorSupplier requestExecutorSupplier;

	@Test
	void shouldReturnRetryExecutorWithDefaults_whenNoProperties() {
		ComponentProperties properties = new ComponentProperties(Map.of());
		RequestExecutorDecorator requestExecutorDecorator = mock(RequestExecutorDecorator.class);
		when(requestExecutorDecorator.with((Retry) any())).thenReturn(requestExecutorDecorator);
		when(requestExecutorDecorator.with((RateLimiter) any())).thenReturn(requestExecutorDecorator);
		RequestExecutor retryRequestExecutor = mock(RequestExecutor.class);
		when(requestExecutorDecorator.get()).thenReturn(retryRequestExecutor);

		try (MockedStatic<RequestExecutorDecorator> utilities = Mockito.mockStatic(RequestExecutorDecorator.class)) {
			utilities.when(() -> RequestExecutorDecorator.decorate(any())).thenReturn(requestExecutorDecorator);
			RequestExecutor result = requestExecutorSupplier.getRequestExecutor(properties);
			assertEquals(result, retryRequestExecutor);
			verify(requestExecutorDecorator).with((Retry) any());
			verify(requestExecutorDecorator).with((RateLimiter) null);
		}
	}

	/**
	 * Required for test purposes. The mock compares on equals which is not implemented in BasicHeader.
	 */
	static class BasicHeaderWithEquals extends BasicHeader {

		public BasicHeaderWithEquals(String name, String value) {
			super(name, value);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Header header)) return false;
			return Objects.equals(getName(), header.getName()) && Objects.equals(getValue(), header.getValue());
		}

		@Override
		public int hashCode() {
			return Objects.hash(getName(), getValue());
		}

	}


	@Test
	void shouldReturnRetryExecutorWithConfiguredProperties_whenPropertiesConfigured() {
		String maxRetries = "10";
		ComponentProperties properties = new ComponentProperties(Map.of(
				MAX_RETRIES, maxRetries,
				STATUSES_TO_RETRY, "400,404",
				AUTH_TYPE, AuthStrategy.API_KEY.name(),
				API_KEY_HEADER, "key-header",
				API_KEY, "key",
				HTTP_HEADERS + ".0.key", "header-key",
				HTTP_HEADERS + ".0.value", "header-value",
				HTTP_HEADERS + ".1.key", "other-header-key",
				HTTP_HEADERS + ".1.value", "other-header-value"));
		RequestExecutor requestExecutor = mock(RequestExecutor.class);
		List<Header> expectedHeaders =
				List.of(
						new BasicHeaderWithEquals("header-key", "header-value"),
						new BasicHeaderWithEquals("other-header-key", "other-header-value"),
						new BasicHeaderWithEquals("key-header", "key")
				);
		when(requestExecutorFactory.createNoAuthExecutor(expectedHeaders)).thenReturn(requestExecutor);
		RequestExecutorDecorator requestExecutorDecorator = mock(RequestExecutorDecorator.class);
		when(requestExecutorDecorator.with((Retry) any())).thenReturn(requestExecutorDecorator);
		when(requestExecutorDecorator.with((RateLimiter) any())).thenReturn(requestExecutorDecorator);
		RequestExecutor retryRequestExecutor = mock(RequestExecutor.class);
		when(requestExecutorDecorator.get()).thenReturn(retryRequestExecutor);

		try (MockedStatic<RequestExecutorDecorator> utilities = Mockito.mockStatic(RequestExecutorDecorator.class)) {
			utilities.when(() -> RequestExecutorDecorator.decorate(any())).thenReturn(requestExecutorDecorator);
			RequestExecutor result = requestExecutorSupplier.getRequestExecutor(properties);
			assertEquals(result, retryRequestExecutor);
			verify(requestExecutorDecorator).with((Retry) any());
			verify(requestExecutorDecorator).with((RateLimiter) null);
		}
	}

	@Test
	void shouldReturnRateLimitExecutorWithConfiguredProperties_whenPropertiesConfigured() {
		ComponentProperties properties = new ComponentProperties(Map.of(
				RATE_LIMIT_ENABLED, "true",
				MAX_REQUESTS_PER_MINUTE, "100"));
		RequestExecutorDecorator requestExecutorDecorator = mock(RequestExecutorDecorator.class);
		when(requestExecutorDecorator.with((Retry) any())).thenReturn(requestExecutorDecorator);
		when(requestExecutorDecorator.with((RateLimiter) any())).thenReturn(requestExecutorDecorator);
		RequestExecutor retryRequestExecutor = mock(RequestExecutor.class);
		when(requestExecutorDecorator.get()).thenReturn(retryRequestExecutor);

		try (MockedStatic<RequestExecutorDecorator> utilities = Mockito.mockStatic(RequestExecutorDecorator.class)) {
			utilities.when(() -> RequestExecutorDecorator.decorate(any())).thenReturn(requestExecutorDecorator);
			RequestExecutor result = requestExecutorSupplier.getRequestExecutor(properties);
			assertEquals(result, retryRequestExecutor);
			verify(requestExecutorDecorator).with((Retry) any());
			verify(requestExecutorDecorator).with((RateLimiter) any());
		}
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
