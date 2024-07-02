package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.PollingIntervalTest.InvalidCronArgumentsProvider;
import be.vlaanderen.informatievlaanderen.ldes.ldio.PollingIntervalTest.InvalidIntervalArgumentsProvider;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpInputPollerAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpInputPollerProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.ConfigPropertyMissingException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.MockedConstruction;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LdioLdioHttpInputPollerAutoConfigTest {
	private final ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
	private final LdiAdapter adapter = mock(LdiAdapter.class);
	private final ComponentExecutor executor = mock(ComponentExecutor.class);
	private static final String BASE_URL = "http://localhost:10101";
	private static final String ENDPOINT = "/resource";

	private static ComponentProperties createConfigWithInterval(String url, String interval, String continueOnFail) {
		return new ComponentProperties("pipelineName", "", Map.of(LdioHttpInputPollerProperties.URL, url,
				LdioHttpInputPollerProperties.INTERVAL, interval,
				LdioHttpInputPollerProperties.CONTINUE_ON_FAIL, continueOnFail));
	}

	private static ComponentProperties createConfigWithCron(String url, String cron, String continueOnFail) {
		return new ComponentProperties("pipelineName", "", Map.of(LdioHttpInputPollerProperties.URL, url,
				LdioHttpInputPollerProperties.CRON, cron,
				LdioHttpInputPollerProperties.CONTINUE_ON_FAIL, continueOnFail));
	}

	private static ComponentProperties createDefaultISOTestConfig() {
		return createConfigWithInterval(BASE_URL + ENDPOINT, "PT1S", "false");
	}

	private static ComponentProperties createDefaultCronTestConfig() {
		return createConfigWithCron(BASE_URL + ENDPOINT, "* * * * * *", "false");
	}

	@Test
	void when_ValidIntervalConfig() {
		try (MockedConstruction<LdioHttpInputPoller> ignored = mockConstruction(LdioHttpInputPoller.class)) {
			LdioHttpInputPoller poller = new LdioHttpInputPollerAutoConfig()
					.httpInputPollerConfigurator(null)
					.configure(adapter, executor, applicationEventPublisher, createDefaultISOTestConfig());
			verify(poller, times(1)).start();
		}
	}

	@Test
	void when_ValidCronConfig() {
		try (MockedConstruction<LdioHttpInputPoller> ignored = mockConstruction(LdioHttpInputPoller.class)) {
			LdioHttpInputPoller poller = new LdioHttpInputPollerAutoConfig()
					.httpInputPollerConfigurator(null)
					.configure(adapter, executor, applicationEventPublisher, createDefaultCronTestConfig());
			verify(poller, times(1)).start();
		}
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidIntervalArgumentsProvider.class)
	void whenInvalidIntervalConfigured_thenCatchException(String interval) {
		ThrowableAssert.ThrowingCallable configurePoller = () -> new LdioHttpInputPollerAutoConfig()
				.httpInputPollerConfigurator(null)
				.configure(adapter, executor, applicationEventPublisher, createConfigWithInterval(BASE_URL + ENDPOINT, interval, "false"));

		assertThatThrownBy(configurePoller).isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidCronArgumentsProvider.class)
	void whenInvalidCronConfigured_thenCatchException(String cron) {
		ThrowableAssert.ThrowingCallable configurePoller = () -> new LdioHttpInputPollerAutoConfig()
				.httpInputPollerConfigurator(null)
				.configure(adapter, executor, applicationEventPublisher, createConfigWithCron(BASE_URL + ENDPOINT, cron, "false"));

		assertThatThrownBy(configurePoller).isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidUrlArgumentsProvider.class)
	void when_MissingUrlConfigProvided_then_ThrowException(String urlKey) {
		final String expectedErrorMessage = "Pipeline \"pipelineName\": \"\" : Missing value for property \"url\" .";

		final Map<String, String> properties = new HashMap<>(Map.of(
				LdioHttpInputPollerProperties.INTERVAL, "PT1S",
				LdioHttpInputPollerProperties.CONTINUE_ON_FAIL, "false"));

		if (urlKey != null) {
			properties.put(urlKey, "http://some-server.com/ldes");
		}

		ComponentProperties componentProperties = new ComponentProperties("pipelineName", "", properties);

		ThrowableAssert.ThrowingCallable configurePoller = () -> new LdioHttpInputPollerAutoConfig()
				.httpInputPollerConfigurator(null).configure(adapter, executor, applicationEventPublisher, componentProperties);

		assertThatThrownBy(configurePoller)
				.isInstanceOf(ConfigPropertyMissingException.class)
				.hasMessage(expectedErrorMessage);
	}

	static class InvalidUrlArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of("endpoint"),
					Arguments.of((Object) null),
					Arguments.of("urls")
			);
		}
	}
}
