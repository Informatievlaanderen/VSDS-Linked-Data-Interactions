package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.config.HttpInputPollerAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.UnsuccesfullPollingException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HttpInputPollerTest {

	private LdiAdapter adapter;
	private String endpoint;
	private ComponentExecutor executor;
	private HttpInputPoller httpInputPoller;

	private static final String CONTENT = "_:b0 <http://schema.org/name> \"Jane Doe\" .";
	private static final String CONTENT_TYPE = "application/n-quads";

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
		httpInputPoller = new HttpInputPoller(executor, adapter, endpoint);
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

	@Test
	void whenPeriodicPolling_thenReturnTwoTimesTheSameResponse() throws InterruptedException {
		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", CONTENT_TYPE)
				.setBody(CONTENT));

		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", CONTENT_TYPE)
				.setBody(CONTENT));

		new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter, executor,
				new ComponentProperties(Map.of("pipelines.input.config.targetUrl", endpoint,
						"pipelines.input.config.interval", "PT1S")));

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

		new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter, executor,
				new ComponentProperties(Map.of("pipelines.input.config.targetUrl", endpoint,
						"pipelines.input.config.interval", "PT1S")));

		verify(adapter, timeout(1500).times(1)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
		verify(adapter, timeout(1500).times(1))
				.apply(LdiAdapter.Content.of(alternativeContent, CONTENT_TYPE));
		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
	}

	@Test
	void whenPeriodPollingReturnsNot2xx_thenKeepPolling() {
		mockBackEnd.enqueue(new MockResponse().setResponseCode(404));
		mockBackEnd.enqueue(new MockResponse().addHeader("Content-Type", CONTENT_TYPE).setBody(CONTENT));

		new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter, executor,
				new ComponentProperties(Map.of("pipelines.input.config.targetUrl", endpoint,
						"pipelines.input.config.interval", "PT1S")));

		verify(adapter, timeout(2000).times(1)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
	}

	@Test
	void when_EndpointDoesNotExist_Then_NoDataIsSent() throws InterruptedException {

		String wrongEndpoint = endpoint = String.format("http://localhst:%s",
				mockBackEnd.getPort());

		httpInputPoller = new HttpInputPoller(executor, adapter, wrongEndpoint);

		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", CONTENT_TYPE)
				.setBody(CONTENT));

		mockBackEnd.enqueue(new MockResponse());

		httpInputPoller.poll();
		verify(adapter,after(1000).never()).apply(any());
	}

	@Test
	void when_ResponseIsNot200_Then_NoDataIsSent() throws InterruptedException {

		httpInputPoller = new HttpInputPoller(executor, adapter, endpoint);
		mockBackEnd.enqueue(new MockResponse().setResponseCode(405));

		httpInputPoller.poll();
		verify(adapter,after(1000).never()).apply(any());
	}
}