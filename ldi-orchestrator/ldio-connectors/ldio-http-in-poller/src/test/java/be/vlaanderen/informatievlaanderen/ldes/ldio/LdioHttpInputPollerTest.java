package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpInputPollerProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PollingInterval;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.MissingHeaderException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioObserver;
import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.support.CronTrigger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.CountMatchingStrategy.GREATER_THAN_OR_EQUAL;
import static com.github.tomakehurst.wiremock.client.CountMatchingStrategy.LESS_THAN_OR_EQUAL;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.*;

@WireMockTest(httpPort = LdioHttpInputPollerTest.WIREMOCK_PORT)
class LdioHttpInputPollerTest {
	public static final int WIREMOCK_PORT = 12321;
	private final LdiAdapter adapter = mock(LdiAdapter.class);
	private final ComponentExecutor executor = mock(ComponentExecutor.class);
	private final ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
	private final static String pipelineName = "pipeline";
	private static final String BASE_URL = "http://localhost:%d".formatted(WIREMOCK_PORT);
	private static final String ENDPOINT = "/resource";
	private static final String CONTENT = "_:b0 <http://schema.org/name> \"Jane Doe\" .";
	private static final String CONTENT_TYPE = "application/n-quads";
	private LdioHttpInputPoller ldioHttpInputPoller;
	private static final RequestExecutor noAuthExecutor = new RequestExecutorFactory().createNoAuthExecutor();

	@BeforeEach
	void setUp() {
		reset();
		when(adapter.apply(any())).thenReturn(Stream.of());
	}

	@AfterEach
	void tearDown() {
		ldioHttpInputPoller.shutdown();
	}

	@Nested
	class DefaultConfig {
		@Test
		void testClientPolling() {
			stubFor(get(ENDPOINT).willReturn(ok().withHeader("Content-Type",
					CONTENT_TYPE).withBody(CONTENT)));

			initPollerWithInterval(null);
			ldioHttpInputPoller.run();
			WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
		}

		@ParameterizedTest
		@ArgumentsSource(PollingIntervalArgumentsProvider.class)
		void whenPeriodicPolling_thenReturnTwoTimesTheSameResponse(PollingInterval pollingInterval) {
			stubFor(get(ENDPOINT).willReturn(ok().withHeader("Content-Type", CONTENT_TYPE).withBody(CONTENT)));

			initPollerWithInterval(pollingInterval);
			ldioHttpInputPoller.start();

			Mockito.verify(adapter, timeout(4000).times(2)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
			WireMock.verify(new CountMatchingStrategy(GREATER_THAN_OR_EQUAL, 2), getRequestedFor(urlEqualTo(ENDPOINT)));
		}

		@ParameterizedTest
		@ArgumentsSource(PollingIntervalArgumentsProvider.class)
		void when_PausePeriodicPolling_then_DontPoll(PollingInterval pollingInterval) {
			stubFor(get(ENDPOINT).willReturn(ok().withHeader("Content-Type", CONTENT_TYPE).withBody(CONTENT)));

			initPollerWithInterval(pollingInterval);
			ldioHttpInputPoller.start();
			ldioHttpInputPoller.pause();

			await().atLeast(2, TimeUnit.SECONDS);
			WireMock.verify(new CountMatchingStrategy(LESS_THAN_OR_EQUAL, 1), getRequestedFor(urlEqualTo(ENDPOINT)));

			ldioHttpInputPoller.resume();

			Mockito.verify(adapter, timeout(4000).times(2)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
			WireMock.verify(new CountMatchingStrategy(GREATER_THAN_OR_EQUAL, 2), getRequestedFor(urlEqualTo(ENDPOINT)));
		}

		@ParameterizedTest
		@ArgumentsSource(PollingIntervalArgumentsProvider.class)
		void when_OnContinueIsTrueAndPeriodPollingReturnsNot2xx_thenKeepPolling(PollingInterval pollingInterval) {
			stubFor(get(ENDPOINT).willReturn(forbidden()));

			initPollerWithInterval(pollingInterval);
			ldioHttpInputPoller.start();

			Mockito.verify(adapter, after(4000).never()).apply(any());
			WireMock.verify(new CountMatchingStrategy(GREATER_THAN_OR_EQUAL, 2),
					getRequestedFor(urlEqualTo(ENDPOINT)));
		}

		@Test
		void when_EndpointDoesNotExist_Then_NoDataIsSent() {
			String wrongEndpoint = "/non-existing-resource";
			initPoller(List.of(BASE_URL + wrongEndpoint), null, true);
			ldioHttpInputPoller.run();

			WireMock.verify(getRequestedFor(urlEqualTo(wrongEndpoint)));
			Mockito.verifyNoInteractions(adapter);
		}

		@Test
		void when_ResponseIsNot200_Then_NoDataIsSent() {
			stubFor(get(ENDPOINT).willReturn(forbidden()));

			initPollerWithInterval(null);
			ldioHttpInputPoller.run();

			WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
			Mockito.verifyNoInteractions(adapter);
		}

		private void initPollerWithInterval(PollingInterval pollingInterval) {
			initPoller(List.of(BASE_URL + ENDPOINT), pollingInterval, true);
		}
	}

	@Test
	void whenPolling_andMissesHeader() {
		stubFor(get(ENDPOINT).willReturn(ok().withBody(CONTENT)));

		initPoller(List.of(BASE_URL + ENDPOINT), null, false);
		Executable polling = () -> ldioHttpInputPoller.run();

		assertThrows(MissingHeaderException.class, polling);
		Mockito.verifyNoInteractions(adapter);
		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
	}


	@Test
	void whenPollMultipleEndpoints_andOneEndpointFails_thenTheOtherEndpointShouldStillBePolled() {
		stubFor(get(ENDPOINT).willReturn(serverError().withHeader("Content-Type", CONTENT_TYPE).withBody(CONTENT)));
		String otherEndpoint = "/other-resource";
		stubFor(get(otherEndpoint).willReturn(ok().withHeader("Content-Type", CONTENT_TYPE).withBody(CONTENT)));
		initPoller(List.of(BASE_URL + ENDPOINT, BASE_URL + otherEndpoint), null, true);

		ldioHttpInputPoller.run();

		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
		WireMock.verify(getRequestedFor(urlEqualTo(otherEndpoint)));
	}

	@ParameterizedTest
	@ArgumentsSource(PollingIntervalArgumentsProvider.class)
	void whenPeriodicPollingMultipleEndpoints_thenReturnTwoTimesTheSameResponse(PollingInterval pollingInterval) {
		String endpoint = "/endpoint";
		stubFor(get(endpoint).willReturn(ok().withHeader("Content-Type", CONTENT_TYPE).withBody(CONTENT)));
		String otherEndpoint = "/other-endpoint";
		stubFor(get(otherEndpoint).willReturn(ok().withHeader("Content-Type", CONTENT_TYPE).withBody(CONTENT)));
		initPoller(List.of(BASE_URL + endpoint, BASE_URL + otherEndpoint), pollingInterval, true);

		ldioHttpInputPoller.start();

		Mockito.verify(adapter, timeout(6000).times(4)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
		WireMock.verify(new CountMatchingStrategy(GREATER_THAN_OR_EQUAL, 2), getRequestedFor(urlEqualTo(endpoint)));
		WireMock.verify(new CountMatchingStrategy(GREATER_THAN_OR_EQUAL, 2), getRequestedFor(urlEqualTo(otherEndpoint)));
	}


	@ParameterizedTest
	@ArgumentsSource(PollingIntervalArgumentsProvider.class)
	void when_OnContinueIsFalseAndPeriodPollingReturnsNot2xx_thenStopPolling(PollingInterval pollingInterval) {
		stubFor(get(ENDPOINT).willReturn(forbidden()));

		initPoller(List.of(BASE_URL + ENDPOINT), pollingInterval, false);
		ldioHttpInputPoller.start();

		Mockito.verify(adapter, after(4000).never()).apply(any());
		WireMock.verify(new CountMatchingStrategy(GREATER_THAN_OR_EQUAL, 1), getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	static class PollingIntervalArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Arguments.of(new PollingInterval(new CronTrigger("*/2 * * * * *"))),
					Arguments.of(new PollingInterval(Duration.of(2, ChronoUnit.SECONDS))));
		}
	}

	private void initPoller(List<String> endpoints, PollingInterval pollingInterval, boolean continueOnFail) {
		final LdioHttpInputPollerProperties properties = new LdioHttpInputPollerProperties(endpoints, pollingInterval, continueOnFail);
		ldioHttpInputPoller = new LdioHttpInputPoller(executor, adapter, LdioObserver.register("Ldio:HttpInPoller", pipelineName, null), noAuthExecutor, properties);
	}

}
