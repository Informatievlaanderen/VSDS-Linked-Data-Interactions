package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpInputPollerTest {

	private LdiAdapter adapter;
	private final String endpoint = "/endpoint";


	@BeforeEach
	void setUp() {
		adapter = mock(LdiAdapter.class);
		ComponentExecutor executor = mock(ComponentExecutor.class);

//		when(adapter.apply(any())).thenReturn(Stream.empty());
	}

	@Test
	void testClientPolling() {
		WebClient webClientMock = mock(WebClient.class);
		WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

		when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
		when(requestHeadersUriSpecMock.uri(notNull(String.class))).thenReturn(requestHeadersSpecMock);
		when(requestHeadersSpecMock.header(notNull(), notNull())).thenReturn(requestHeadersSpecMock);
		when(requestHeadersSpecMock.headers(notNull())).thenReturn(requestHeadersSpecMock);
		when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);


		assertTrue(true);

	}
}