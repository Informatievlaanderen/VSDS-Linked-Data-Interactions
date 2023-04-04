package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.HttpInputPollerAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.InvalidPollerConfigException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;


import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WireMockTest(httpPort = 10101)
class HttpInputPollerTest {
	private LdiAdapter adapter;
//	private static final String BASE_URL = "http://localhost:10101";
	private static final String ENDPOINT = "/resource";
	private static String endpoint = "";
	private ComponentExecutor executor;
	private HttpInputPoller httpInputPoller;
	private ScheduledExecutorService scheduledExecutorService;

	private static final String CONTENT = "_:b0 <http://schema.org/name> \"Jane Doe\" .";
	private static final String CONTENT_TYPE = "application/n-quads";

	private static ComponentProperties createConfig(String endpoint, String interval, String continueOnFail) {
		return new ComponentProperties(Map.of("url", endpoint, "interval", interval, "continueOnFail", continueOnFail));
	}

	private static ComponentProperties createDefaultTestConfig() {
		return createConfig(endpoint, "PT1S", "false");
	}

	public static MockWebServer mockBackEnd;

	@BeforeEach
	void setUp() {
		final String baseUrl = "http://localhost:10101";
		adapter = mock(LdiAdapter.class);
		executor = mock(ComponentExecutor.class);

		when(adapter.apply(any())).thenReturn(Stream.empty());

		httpInputPoller = new HttpInputPoller(executor, adapter, baseUrl + ENDPOINT, true);
	}

	@AfterEach
	void tearDown() {
		if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
			scheduledExecutorService.shutdown();
		}
	}

	@Test
	void testClientPolling() {
		stubFor(get(urlEqualTo(ENDPOINT)).willReturn(ok().withHeader("Content-Type",
				CONTENT_TYPE).withBody(CONTENT)));

		httpInputPoller.poll();
		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidIntervalArgumentsProvider.class)
	@Disabled
	void whenInvalidIntervalConfigured_thenCatchException(String interval) {
		Executable configurePoller = () -> scheduledExecutorService = new HttpInputPollerAutoConfig()
				.httpInputPollerConfigurator().configure(adapter, executor, createConfig(endpoint, interval, "false"));

		assertThrows(InvalidPollerConfigException.class, configurePoller);
	}

	@Test
	@Disabled
	void whenPeriodicPolling_thenReturnTwoTimesTheSameResponse() throws InterruptedException {
		mockBackEnd.enqueue(new MockResponse().addHeader("Content-Type", CONTENT_TYPE).setBody(CONTENT));

		mockBackEnd.enqueue(new MockResponse().addHeader("Content-Type", CONTENT_TYPE).setBody(CONTENT));

		scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter,
				executor, createDefaultTestConfig());

		verify(adapter, timeout(3000).atLeast(2)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
	}

	@Test
	@Disabled
	void whenPeriodicPolling_thenReturnDifferentResponses() throws InterruptedException {
		final String alternativeContent = "_:b0 <http://schema.org/name> \"John Doe\" .";
		mockBackEnd.enqueue(new MockResponse().addHeader("Content-Type", CONTENT_TYPE).setBody(CONTENT));

		mockBackEnd.enqueue(new MockResponse().addHeader("Content-Type", CONTENT_TYPE).setBody(alternativeContent));

		scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter,
				executor, createDefaultTestConfig());

		verify(adapter, timeout(1500).times(1)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
		verify(adapter, timeout(1500).times(1)).apply(LdiAdapter.Content.of(alternativeContent, CONTENT_TYPE));
		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
	}

	@Test
	@Disabled
	void when_OnContinueIsTrueAndPeriodPollingReturnsNot2xx_thenKeepPolling() {
		mockBackEnd.enqueue(new MockResponse().setResponseCode(404));
		mockBackEnd.enqueue(new MockResponse().addHeader("Content-Type", CONTENT_TYPE).setBody(CONTENT));

		scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter,
				executor, createConfig(endpoint, "PT1S", "true"));

		verify(adapter, timeout(2000).times(1)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
	}

	@Test
	@Disabled
	void when_OnContinueIsFalseAndPeriodPollingReturnsNot2xx_thenStopPolling() {
		mockBackEnd.enqueue(new MockResponse().setResponseCode(404));
		mockBackEnd.enqueue(new MockResponse().addHeader("Content-Type", CONTENT_TYPE).setBody(CONTENT));

		scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter,
				executor, createDefaultTestConfig());

		verify(adapter, after(2000).never()).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
	}

	@Test
	@Disabled
	void when_EndpointDoesNotExist_Then_NoDataIsSent() {

		String wrongEndpoint = endpoint = String.format("http://localhst:%s", mockBackEnd.getPort());
		httpInputPoller = new HttpInputPoller(executor, adapter, wrongEndpoint, true);

		mockBackEnd.enqueue(new MockResponse().addHeader("Content-Type", CONTENT_TYPE).setBody(CONTENT));

		mockBackEnd.enqueue(new MockResponse());

		httpInputPoller.poll();
		verify(adapter, after(1000).never()).apply(any());
	}

	@Test
	@Disabled
	void when_ResponseIsNot200_Then_NoDataIsSent() {

		mockBackEnd.enqueue(
				new MockResponse().setResponseCode(405).addHeader("Content-Type", CONTENT_TYPE).setBody(CONTENT));

		httpInputPoller.poll();
		verify(adapter, after(1000).never()).apply(any());
	}

	static class InvalidIntervalArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Arguments.of("P12S"), Arguments.of("Invalid time"), Arguments.of("0 * * * * ?"));
		}
	}
}
