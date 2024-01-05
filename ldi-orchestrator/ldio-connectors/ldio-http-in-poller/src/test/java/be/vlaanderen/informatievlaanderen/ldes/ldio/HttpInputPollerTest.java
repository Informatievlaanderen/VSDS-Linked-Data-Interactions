package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PollingInterval;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.MissingHeaderException;
import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
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
import org.springframework.scheduling.support.CronTrigger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.*;

@WireMockTest(httpPort = 10101)
class HttpInputPollerTest {
	private final LdiAdapter adapter = mock(LdiAdapter.class);
	private final ComponentExecutor executor = mock(ComponentExecutor.class);
	private final static String pipelineName = "pipeline";
	private static final String BASE_URL = "http://localhost:10101";
	private static final String ENDPOINT = "/resource";
	private static final String CONTENT = "_:b0 <http://schema.org/name> \"Jane Doe\" .";
	private static final String CONTENT_TYPE = "application/n-quads";
	private HttpInputPoller httpInputPoller;
	private static final RequestExecutor noAuthExecutor = new RequestExecutorFactory().createNoAuthExecutor();
	@BeforeEach
    void setUp() {
		reset();

        when(adapter.apply(any()))
                .thenReturn(Stream.of())
                .thenReturn(Stream.of())
                .thenReturn(Stream.of())
                .thenReturn(Stream.of());

		httpInputPoller = new HttpInputPoller(pipelineName, executor, adapter, null, List.of(BASE_URL + ENDPOINT), true, noAuthExecutor);
    }

	@AfterEach
	void tearDown() {
		httpInputPoller.shutdown();
	}

	@Test
	void testClientPolling() {
		stubFor(get(ENDPOINT).willReturn(ok().withHeader("Content-Type",
				CONTENT_TYPE).withBody(CONTENT)));

		httpInputPoller.run();
		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@Test
	void whenPolling_andMissesHeader() {
		stubFor(get(ENDPOINT).willReturn(ok().withBody(CONTENT)));

		httpInputPoller = new HttpInputPoller(pipelineName, executor, adapter, null, List.of(BASE_URL + ENDPOINT), false, noAuthExecutor);
		Executable polling = () -> httpInputPoller.run();

		assertThrows(MissingHeaderException.class, polling);
		Mockito.verifyNoInteractions(adapter);
		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@ParameterizedTest
	@ArgumentsSource(PollingIntervalArgumentsProvider.class)
	void whenPeriodicPolling_thenReturnTwoTimesTheSameResponse(PollingInterval pollingInterval) {
		stubFor(get(ENDPOINT).willReturn(ok().withHeader("Content-Type", CONTENT_TYPE).withBody(CONTENT)));

		httpInputPoller.schedulePoller(pollingInterval);

		Mockito.verify(adapter, timeout(4000).times(2)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
		WireMock.verify(2, getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@Test
	void whenPollMultipleEndpoints_andOneEndpointFails_thenTheOtherEndpointShouldStillBePolled() {
		stubFor(get(ENDPOINT).willReturn(serverError().withHeader("Content-Type", CONTENT_TYPE).withBody(CONTENT)));
		String otherEndpoint = "/other-resource";
		stubFor(get(otherEndpoint).willReturn(ok().withHeader("Content-Type", CONTENT_TYPE).withBody(CONTENT)));
		httpInputPoller = new HttpInputPoller(pipelineName, executor, adapter, null, List.of(BASE_URL + ENDPOINT, BASE_URL + otherEndpoint),
				true, noAuthExecutor);

		httpInputPoller.run();

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
		httpInputPoller = new HttpInputPoller(pipelineName, executor, adapter, null, List.of(BASE_URL + endpoint, BASE_URL + otherEndpoint),
				true, noAuthExecutor);

		httpInputPoller.schedulePoller(pollingInterval);

		Mockito.verify(adapter, timeout(6000).times(4)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
		WireMock.verify(2, getRequestedFor(urlEqualTo(endpoint)));
		WireMock.verify(2, getRequestedFor(urlEqualTo(otherEndpoint)));
	}

	@ParameterizedTest
	@ArgumentsSource(PollingIntervalArgumentsProvider.class)
	void when_OnContinueIsTrueAndPeriodPollingReturnsNot2xx_thenKeepPolling(PollingInterval pollingInterval) {
		stubFor(get(ENDPOINT).willReturn(forbidden()));

		httpInputPoller.schedulePoller(pollingInterval);

		Mockito.verify(adapter, after(4000).never()).apply(any());
		WireMock.verify(new CountMatchingStrategy(CountMatchingStrategy.GREATER_THAN_OR_EQUAL, 2),
				getRequestedFor(urlEqualTo(ENDPOINT)));

	}

	@ParameterizedTest
	@ArgumentsSource(PollingIntervalArgumentsProvider.class)
	void when_OnContinueIsFalseAndPeriodPollingReturnsNot2xx_thenStopPolling(PollingInterval pollingInterval) {
		stubFor(get(ENDPOINT).willReturn(forbidden()));

		httpInputPoller = new HttpInputPoller(pipelineName, executor, adapter, null, List.of(BASE_URL + ENDPOINT), false, noAuthExecutor);
		httpInputPoller.schedulePoller(pollingInterval);

		Mockito.verify(adapter, after(2000).never()).apply(any());
		WireMock.verify(1, getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@ParameterizedTest
	@ArgumentsSource(PollingIntervalArgumentsProvider.class)
	void when_OnContinueIsFalseAndPeriodPollingReturnsNot2xx_thenStopPolling_Cron(PollingInterval pollingInterval) {
		stubFor(get(ENDPOINT).willReturn(forbidden()));

		httpInputPoller = new HttpInputPoller(pipelineName, executor, adapter, null, List.of(BASE_URL + ENDPOINT), false, noAuthExecutor);
		httpInputPoller.schedulePoller(pollingInterval);

		Mockito.verify(adapter, after(2000).never()).apply(any());
		WireMock.verify(1, getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@Test
	void when_EndpointDoesNotExist_Then_NoDataIsSent() {
		String wrongEndpoint = "/non-existing-resource";
		httpInputPoller = new HttpInputPoller(pipelineName, executor, adapter, null, List.of(BASE_URL + wrongEndpoint), true,
				noAuthExecutor);

		httpInputPoller.run();

		WireMock.verify(getRequestedFor(urlEqualTo(wrongEndpoint)));
		Mockito.verifyNoInteractions(adapter);
	}

	@Test
	void when_ResponseIsNot200_Then_NoDataIsSent() {
		stubFor(get(ENDPOINT).willReturn(forbidden()));

		httpInputPoller.run();

		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
		Mockito.verifyNoInteractions(adapter);
	}

	static class PollingIntervalArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Arguments.of(new PollingInterval(new CronTrigger("*/2 * * * * *"))),
					Arguments.of(new PollingInterval(Duration.of(2, ChronoUnit.SECONDS))));
		}
	}

}
