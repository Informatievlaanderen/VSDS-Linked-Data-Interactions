package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.InvalidPollerConfigException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.HttpInputPollerAutoConfig;
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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HttpInputPollerTest {
	private LdiAdapter adapter;
	private static String endpoint;
	private ComponentExecutor executor;
	private HttpInputPoller httpInputPoller;
	private ScheduledExecutorService scheduledExecutorService;

	private static final String CONTENT = "_:b0 <http://schema.org/name> \"Jane Doe\" .";
	private static final String CONTENT_TYPE = "application/n-quads";

	private static ComponentProperties createConfig(String endpoint, String interval, String continueOnFail) {
		return new ComponentProperties(Map.of("pipelines.input.config.targetUrl", endpoint,
				"pipelines.input.config.interval", interval, "pipelines.input.config.continueOnFail", continueOnFail));
	}

	private static ComponentProperties createDefaultTestConfig() {
		return createConfig(endpoint, "PT1S", "false");
	}

	public static MockWebServer mockBackEnd;

	@BeforeAll
	static void beforeAll() throws IOException {
		mockBackEnd = new MockWebServer();
		mockBackEnd.start();
	}

	@AfterAll
	static void afterAll() throws IOException {
		mockBackEnd.close();
	}

	@BeforeEach
	void setUp() throws IOException {
		adapter = mock(LdiAdapter.class);
		executor = mock(ComponentExecutor.class);

		when(adapter.apply(any())).thenReturn(Stream.empty());

		mockBackEnd = new MockWebServer();
		mockBackEnd.start();
		endpoint = String.format("http://localhost:%s", mockBackEnd.getPort());
		httpInputPoller = new HttpInputPoller(executor, adapter, endpoint, true);
	}

	@AfterEach
	void tearDown() {
		if(scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
			scheduledExecutorService.shutdown();
		}
	}

	@Test
	void testClientPolling() throws InterruptedException {
		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", CONTENT_TYPE)
				.setBody(CONTENT));

		httpInputPoller.poll();

		verify(adapter, timeout(1000).times(1))
				.apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));

		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidIntervalArgumentsProvider.class)
	void whenInvalidIntervalConfigured_thenCatchException(String interval) {
		Executable configurePoller = () -> scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator()
				.configure(adapter, executor, createConfig(endpoint, interval, "false"));

		assertThrows(InvalidPollerConfigException.class, configurePoller);
	}

	@Test
	void whenPeriodicPolling_thenReturnTwoTimesTheSameResponse() throws InterruptedException {
		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", CONTENT_TYPE)
				.setBody(CONTENT));

		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", CONTENT_TYPE)
				.setBody(CONTENT));

		scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter, executor,
				createDefaultTestConfig());

		verify(adapter, timeout(3000).atLeast(2))
				.apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
	}

	@Test
	void whenPeriodicPolling_thenReturnDifferentResponses() throws InterruptedException {
		final String alternativeContent = "_:b0 <http://schema.org/name> \"John Doe\" .";
		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", CONTENT_TYPE)
				.setBody(CONTENT));

		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", CONTENT_TYPE)
				.setBody(alternativeContent));

		scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter, executor,
				createDefaultTestConfig());

		verify(adapter, timeout(1500).times(1)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
		verify(adapter, timeout(1500).times(1))
				.apply(LdiAdapter.Content.of(alternativeContent, CONTENT_TYPE));
		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
	}

	@Test
	void when_OnContinueIsTrueAndPeriodPollingReturnsNot2xx_thenKeepPolling() {
		mockBackEnd.enqueue(new MockResponse().setResponseCode(404));
		mockBackEnd.enqueue(new MockResponse().addHeader("Content-Type", CONTENT_TYPE).setBody(CONTENT));

		scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter, executor,
				createConfig(endpoint, "PT1S", "true"));

		verify(adapter, timeout(2000).times(1)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
	}

	@Test
	void when_OnContinueIsFalseAndPeriodPollingReturnsNot2xx_thenStopPolling() {
		mockBackEnd.enqueue(new MockResponse().setResponseCode(404));
		mockBackEnd.enqueue(new MockResponse().addHeader("Content-Type", CONTENT_TYPE).setBody(CONTENT));

		scheduledExecutorService = new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter, executor,
				createDefaultTestConfig());

		verify(adapter, after(2000).never()).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
	}

	@Test
	void when_EndpointDoesNotExist_Then_NoDataIsSent() {

		String wrongEndpoint = endpoint = String.format("http://localhst:%s",
				mockBackEnd.getPort());
		httpInputPoller = new HttpInputPoller(executor, adapter, wrongEndpoint, true);

		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", CONTENT_TYPE)
				.setBody(CONTENT));

		mockBackEnd.enqueue(new MockResponse());

		httpInputPoller.poll();
		verify(adapter, after(1000).never()).apply(any());
	}

	@Test
	void when_ResponseIsNot200_Then_NoDataIsSent() {

		mockBackEnd.enqueue(new MockResponse().setResponseCode(405)
				.addHeader("Content-Type", CONTENT_TYPE)
				.setBody(CONTENT));

		httpInputPoller.poll();
		verify(adapter, after(1000).never()).apply(any());
	}

	static class InvalidIntervalArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of("P12S"),
					Arguments.of("Invalid time"),
					Arguments.of("0 * * * * ?"));
		}
	}
}
