package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials.ClientCredentialsRequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.DefaultRequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.RetryExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LdioLdesClientAutoConfigTest {
	private static final String ENDPOINT = "http://localhost:8080/endpoint";
	private static final String pipelineName = "pipeline";
	private LdioLdesClientConfigurator configurator;
	@Mock
	private LdiAdapter adapter;

	@Mock
	private ComponentExecutor componentExecutor;

	@BeforeEach
	void setUp() {
		LdioLdesClientAutoConfig autoConfig = new LdioLdesClientAutoConfig();
		configurator = (LdioLdesClientConfigurator) autoConfig.ldioConfigurator();
	}

	@Test
	void when_invalidConfigProvided_then_noInteractionsExpected() {
		configurator.configure(adapter, componentExecutor,
				new ComponentProperties(Map.of(PIPELINE_NAME, pipelineName)));

		verifyNoInteractions(componentExecutor);
	}

	@ParameterizedTest
	@ArgumentsSource(RequestExecutorConfigArgumentsProvider.class)
	void when_validConfigProvided_then_requestExecutorCreated(ComponentProperties props, Class<RequestExecutor> cls) {
		LdioLdesClientConfigurator spyConfigurator = spy(configurator);

		spyConfigurator.configure(adapter, componentExecutor, props);
		verify(spyConfigurator).getRequestExecutorWithPossibleRetry(props);

		RequestExecutor executor = configurator.getRequestExecutorWithPossibleRetry(props);
		assertEquals(cls, executor.getClass());
	}

	@ParameterizedTest
	@ValueSource(strings = { "api_key", "oauth2_client_credentials" })
	void when_autTypeProvided_and_additionalKeysAreMissing_then_throwException(String authType) {
		ComponentProperties props = new ComponentProperties(
				Map.of(PIPELINE_NAME, pipelineName, URL, ENDPOINT, AUTH_TYPE, authType));

		assertThrows(IllegalArgumentException.class, () -> configurator.configure(adapter, componentExecutor, props));
	}

	@Test
	void when_unsupportedAuthTypeProvided_then_throwException() {
		final String INVALID_AUTH_TYPE = "invalid_auth_type";
		ComponentProperties props = new ComponentProperties(
				Map.of(PIPELINE_NAME, pipelineName, URL, ENDPOINT, AUTH_TYPE, INVALID_AUTH_TYPE));

		Exception e = assertThrows(UnsupportedOperationException.class,
				() -> configurator.configure(adapter, componentExecutor, props));
		assertEquals("Requested authentication not available: " + INVALID_AUTH_TYPE, e.getMessage());
	}

	private static class RequestExecutorConfigArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(new ComponentProperties(Map.of(PIPELINE_NAME, pipelineName, RETRIES_ENABLED, "FALSE",
							URL, ENDPOINT)),
							DefaultRequestExecutor.class),
					Arguments.of(
							new ComponentProperties(
									Map.of(PIPELINE_NAME, pipelineName, RETRIES_ENABLED, "false", URL, ENDPOINT,
											AUTH_TYPE, "api_key", API_KEY, "my_secret_key")),
							DefaultRequestExecutor.class),
					Arguments.of(
							new ComponentProperties(Map.of(
									PIPELINE_NAME, pipelineName,
									RETRIES_ENABLED, "false",
									URL, ENDPOINT,
									AUTH_TYPE, "oauth2_client_credentials",
									CLIENT_ID, "client_id",
									CLIENT_SECRET, "my_client_secret",
									TOKEN_ENDPOINT, "http://localhost:8080/token-endpoint")),
							ClientCredentialsRequestExecutor.class),
					Arguments.of(
							new ComponentProperties(Map.of(PIPELINE_NAME, pipelineName, URL, ENDPOINT,
									RETRIES_ENABLED, "FALSE")),
							DefaultRequestExecutor.class),
					Arguments.of(
							new ComponentProperties(Map.of(PIPELINE_NAME, pipelineName, URL, ENDPOINT,
									RETRIES_ENABLED, "true")),
							RetryExecutor.class),
					Arguments.of(
							new ComponentProperties(Map.of(
									PIPELINE_NAME, pipelineName,
									URL, ENDPOINT,
									RETRIES_ENABLED, "TRUE",
									MAX_RETRIES, "10",
									AUTH_TYPE, "oauth2_client_credentials",
									CLIENT_ID, "client_id",
									CLIENT_SECRET, "my_client_secret",
									TOKEN_ENDPOINT, "http://localhost:8080/token-endpoint")),
							RetryExecutor.class));
		}
	}
}
