package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.HttpInputPollerAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.MissingHeaderException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.HttpInputPollerProperties.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.after;

@WireMockTest(httpPort = 10101)
class HttpInputPollerTest {
	private final LdiAdapter adapter = mock(LdiAdapter.class);
	private final ComponentExecutor executor = mock(ComponentExecutor.class);
	private static final String BASE_URL = "http://localhost:10101";
	private static final String ENDPOINT = "/resource";
	private static final String CONTENT = "_:b0 <http://schema.org/name> \"Jane Doe\" .";
	private static final String CONTENT_TYPE = "application/n-quads";
	private HttpInputPoller httpInputPoller;
	private ScheduledExecutorService scheduledExecutorService;

	@BeforeEach
    void setUp() {
        when(adapter.apply(any())).thenReturn(Stream.empty());

        httpInputPoller = new HttpInputPoller(executor, adapter, BASE_URL + ENDPOINT, true);
    }

	@AfterEach
	void tearDown() {
		if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
			scheduledExecutorService.shutdown();
		}
	}

	@Test
	void testClientPolling() {
		stubFor(get(ENDPOINT).willReturn(ok().withHeader("Content-Type",
				CONTENT_TYPE).withBody(CONTENT)));

		httpInputPoller.poll();
		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidIntervalArgumentsProvider.class)
	void whenInvalidIntervalConfigured_thenCatchException(String interval) {
		Executable configurePoller = () -> scheduledExecutorService = new HttpInputPollerAutoConfig()
				.httpInputPollerConfigurator()
				.configure(adapter, executor, createConfig(BASE_URL + ENDPOINT, interval, "false"));

		assertThrows(IllegalArgumentException.class, configurePoller);
	}

	@Test
	void whenPolling_andMissesHeader() {
		stubFor(get(ENDPOINT).willReturn(ok().withBody(CONTENT)));

		httpInputPoller = new HttpInputPoller(executor, adapter, BASE_URL + ENDPOINT, false);
		Executable polling = () -> httpInputPoller.poll();

		assertThrows(MissingHeaderException.class, polling);
		Mockito.verifyNoInteractions(adapter);
		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@Test
	void whenPeriodicPolling_thenReturnTwoTimesTheSameResponse() {
		stubFor(get(ENDPOINT).willReturn(ok().withHeader("Content-Type", CONTENT_TYPE).withBody(CONTENT)));

		scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter,
				executor, createDefaultTestConfig());

		Mockito.verify(adapter, timeout(2500).atLeast(2)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
		WireMock.verify(2, getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@Test
	void when_OnContinueIsTrueAndPeriodPollingReturnsNot2xx_thenKeepPolling() {
		stubFor(get(ENDPOINT).willReturn(forbidden()));

		scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter,
				executor, createConfig(BASE_URL + ENDPOINT, "PT1S", "true"));

		Mockito.verify(adapter, after(2000).never()).apply(any());
		WireMock.verify(new CountMatchingStrategy(CountMatchingStrategy.GREATER_THAN_OR_EQUAL, 2),
				getRequestedFor(urlEqualTo(ENDPOINT)));

	}

	@Test
	void when_OnContinueIsFalseAndPeriodPollingReturnsNot2xx_thenStopPolling() {
		stubFor(get(ENDPOINT).willReturn(forbidden()));

		scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter,
				executor, createDefaultTestConfig());

		Mockito.verify(adapter, after(2000).never()).apply(any());
		WireMock.verify(1, getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@Test
	void when_EndpointDoesNotExist_Then_NoDataIsSent() {
		String wrongEndpoint = "/non-existing-resource";
		httpInputPoller = new HttpInputPoller(executor, adapter, BASE_URL + wrongEndpoint, true);

		httpInputPoller.poll();

		WireMock.verify(getRequestedFor(urlEqualTo(wrongEndpoint)));
		Mockito.verifyNoInteractions(adapter);
	}

	@Test
	void when_ResponseIsNot200_Then_NoDataIsSent() {
		stubFor(get(ENDPOINT).willReturn(forbidden()));

		httpInputPoller.poll();

		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
		Mockito.verifyNoInteractions(adapter);
	}

	private static ComponentProperties createConfig(String url, String interval, String continueOnFail) {
		return new ComponentProperties(Map.of(URL, url, INTERVAL, interval, CONTINUE_ON_FAIL, continueOnFail));
	}

	private static ComponentProperties createDefaultTestConfig() {
		return createConfig(BASE_URL + ENDPOINT, "PT1S", "false");
	}

	static class InvalidIntervalArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Arguments.of("P12S"), Arguments.of("Invalid time"), Arguments.of("0 * * * * ?"));
		}
	}

}
