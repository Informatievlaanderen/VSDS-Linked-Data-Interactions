package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.io.IOException;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

class HttpInputPollerTest {

	private LdiAdapter adapter;
	private String endpoint;
	private HttpInputPoller httpInputPoller;
	private WebClient client;
	private ComponentExecutor executor;

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


		client = mock(WebClient.class);
		WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

		when(client.get()).thenReturn(requestHeadersUriSpecMock);
		when(requestHeadersUriSpecMock.uri(notNull(String.class))).thenReturn(requestHeadersSpecMock);
		when(requestHeadersSpecMock.header(notNull(), notNull())).thenReturn(requestHeadersSpecMock);
		when(requestHeadersSpecMock.headers(notNull())).thenReturn(requestHeadersSpecMock);
		when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
	}

	@Test
	void testClientPolling() throws InterruptedException {
//		httpInputPoller = new HttpInputPoller(executor, adapter, client);
//		httpInputPoller.poll();
//		verify(client, times(1)).get();

		httpInputPoller = new HttpInputPoller(executor, adapter, endpoint);

		mockBackEnd.enqueue(new MockResponse()
				.addHeader("Content-Type", "application/n-quads")
				.setBody("_:b0 <http://schema.org/name> \"Jane Doe\" .")
		);

		httpInputPoller.poll();

		Thread.sleep(1000);

		verify(adapter, times(1)).apply(LdiAdapter.Content.of("_:b0 <http://schema.org/name> \"Jane Doe\" .", "application/n-quads"));
		RecordedRequest recordedRequest = mockBackEnd.takeRequest();
		assertEquals("GET", recordedRequest.getMethod());
	}
}