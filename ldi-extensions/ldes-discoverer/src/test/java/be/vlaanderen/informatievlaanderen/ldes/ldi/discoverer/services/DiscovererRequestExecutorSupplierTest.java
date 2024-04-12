package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.config.RequestExecutorProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects.Headers;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials.ClientCredentialsRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials.ClientCredentialsRequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.DefaultRequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.Collection;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.HeadersMatcher.containsAllHeaders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscovererRequestExecutorSupplierTest {
	@Mock
	private ApplicationArguments arguments;
	@InjectMocks
	private RequestExecutorProperties properties;
	@Spy
	private RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory(false);
	private DiscovererRequestExecutorSupplier requestExecutorSupplier;

	@BeforeEach
	void setUp() {
		requestExecutorSupplier = new DiscovererRequestExecutorSupplier(properties, requestExecutorFactory);
	}

	@Test
	void given_DefaultConfig_when_CreateExecutor_then_ReturnDefaultExecutorWithAdditionalHeaders() {
		when(arguments.getOptionValues(anyString())).thenReturn(null);

		final RequestExecutor actual = requestExecutorSupplier.createRequestExecutor();

		assertThat(actual)
				.isInstanceOf(RequestExecutor.class)
				.isNotInstanceOfAny(DefaultRequestExecutor.class, ClientCredentialsRequest.class);
		verify(requestExecutorFactory).createNoAuthExecutor(argThat(containsAllHeaders(List.of())));
	}

	@Test
	void given_ApiKeyAuthConfig_when_CreateExecutor_then_ReturnDefaultExecutorWithAdditionalHeaders() {
		when(arguments.getOptionValues(anyString())).thenReturn(null);
		when(arguments.getOptionValues("auth-type")).thenReturn(List.of(AuthStrategy.API_KEY.name()));
		when(arguments.getOptionValues("api-key")).thenReturn(List.of("my-secret-api-key"));

		final RequestExecutor actual = requestExecutorSupplier.createRequestExecutor();

		assertThat(actual)
				.isInstanceOf(RequestExecutor.class)
				.isNotInstanceOfAny(DefaultRequestExecutor.class, ClientCredentialsRequest.class) ;
		verify(arguments).getOptionValues("auth-type");
		verify(requestExecutorFactory).createNoAuthExecutor(argThat(containsAllHeaders(List.of(new BasicHeader("X-API-KEY", "my-secret-api-key")))));
	}

	@Test
	void given_NoDecorationConfigIsProvided_when_CreateExecutor_then_ReturnBaseExecutor() {
		when(arguments.containsOption("disable-retry")).thenReturn(true);
		when(arguments.containsOption("enable-rate-limit")).thenReturn(false);

		final RequestExecutor actual = requestExecutorSupplier.createRequestExecutor();

		assertThat(actual).isInstanceOf(DefaultRequestExecutor.class);
		verify(requestExecutorFactory).createNoAuthExecutor(argThat(containsAllHeaders(List.of())));
	}

	@Test
	void given_ClientCredentialsAuthConfig_when_CreateExecutor_then_ReturnDefaultExecutorWithAdditionalHeaders() {
		when(arguments.containsOption("disable-retry")).thenReturn(true);
		when(arguments.containsOption("enable-rate-limit")).thenReturn(false);
		when(arguments.getOptionValues("header")).thenReturn(null);
		when(arguments.getOptionValues("auth-type")).thenReturn(List.of(AuthStrategy.OAUTH2_CLIENT_CREDENTIALS.name()));
		when(arguments.getOptionValues("client-id")).thenReturn(List.of("my-client-id"));
		when(arguments.getOptionValues("client-secret")).thenReturn(List.of("my-client-secret"));
		when(arguments.getOptionValues("token-endpoint")).thenReturn(List.of("my-token-endpoint"));

		final RequestExecutor actual = requestExecutorSupplier.createRequestExecutor();

		assertThat(actual).isInstanceOf(ClientCredentialsRequestExecutor.class);
		verify(requestExecutorFactory).createClientCredentialsExecutor(argThat(Collection::isEmpty), anyString(), anyString(), anyString(), isNull());
	}

	@Test
	void given_RateLimitConfig_when_CreateExecutor_then_ReturnLambda() {
		when(arguments.containsOption("enable-rate-limit")).thenReturn(true);

		final RequestExecutor actual = requestExecutorSupplier.createRequestExecutor();

		assertThat(actual)
				.isInstanceOf(RequestExecutor.class)
				.isNotInstanceOfAny(DefaultRequestExecutor.class, ClientCredentialsRequest.class) ;
		verify(arguments).containsOption("enable-rate-limit");
	}
}