package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.config.HttpInputPollerAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
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
		endpoint = String.format("http://localhost:%s",
				mockBackEnd.getPort());
		httpInputPoller = new HttpInputPoller(executor, adapter, endpoint);
	}

	@Test
	void testClientPolling() throws InterruptedException {

		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", "application/n-quads")
				.setBody("_:b0 <http://schema.org/name> \"Jane Doe\" ."));

		httpInputPoller.poll();

		verify(adapter, timeout(1000).times(1))
				.apply(LdiAdapter.Content.of("_:b0 <http://schema.org/name> \"Jane Doe\" .", "application/n-quads"));

		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
	}

	@Test
	void whenPeriodicPolling_thenReturnTwoTimesTheSameResponse() throws InterruptedException {
		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", "application/n-quads")
				.setBody("_:b0 <http://schema.org/name> \"Jane Doe\" ."));

		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", "application/n-quads")
				.setBody("_:b0 <http://schema.org/name> \"Jane Doe\" ."));

		new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter, executor,
				new ComponentProperties(Map.of("pipelines.input.config.targetUrl", endpoint,
						"pipelines.input.config.interval", "PT1S")));

		verify(adapter, timeout(3000).atLeast(2))
				.apply(LdiAdapter.Content.of("_:b0 <http://schema.org/name> \"Jane Doe\" .", "application/n-quads"));
		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
	}

	@Test
	void whenPeriodicPolling_thenReturnDifferentResponses() throws InterruptedException {
		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", "application/n-quads")
				.setBody("_:b0 <http://schema.org/name> \"Jane Doe\" ."));

		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", "application/n-quads")
				.setBody("_:b0 <http://schema.org/name> \"John Doe\" ."));

		new HttpInputPollerAutoConfig().httpInputPollerConfigurator().configure(adapter, executor,
				new ComponentProperties(Map.of("pipelines.input.config.targetUrl", endpoint,
						"pipelines.input.config.interval", "PT1S")));

		verify(adapter, timeout(1500).times(1))
				.apply(LdiAdapter.Content.of("_:b0 <http://schema.org/name> \"Jane Doe\" .", "application/n-quads"));
		verify(adapter, timeout(1500).times(1))
				.apply(LdiAdapter.Content.of("_:b0 <http://schema.org/name> \"John Doe\" .", "application/n-quads"));
		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
	}

	@Test
	void when_EndpointDoesNotExist() throws InterruptedException {

		String wrongEndpoint = endpoint = String.format("http://localhst:%s",
				mockBackEnd.getPort());

		httpInputPoller = new HttpInputPoller(executor, adapter, wrongEndpoint);

		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", "application/n-quads")
				.setBody("_:b0 <http://schema.org/name> \"Jane Doe\" .")
		);

		httpInputPoller.poll();


	}
}