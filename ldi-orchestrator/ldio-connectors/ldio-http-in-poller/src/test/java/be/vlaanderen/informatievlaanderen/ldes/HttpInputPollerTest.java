package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpInputPollerTest {

	private LdiAdapter adapter;
	private String endpoint;

	public static MockWebServer mockBackEnd;

	@BeforeEach
	void setUp() throws IOException {
		adapter = mock(LdiAdapter.class);
		ComponentExecutor executor = mock(ComponentExecutor.class);


		mockBackEnd = new MockWebServer();
		mockBackEnd.start();
		endpoint = String.format("http://localhost:%s",
				mockBackEnd.getPort());
	}

	@Test
	void testClientPolling() throws JsonProcessingException {

		mockBackEnd.enqueue(new MockResponse()
				.setBody("value"));


	}
}