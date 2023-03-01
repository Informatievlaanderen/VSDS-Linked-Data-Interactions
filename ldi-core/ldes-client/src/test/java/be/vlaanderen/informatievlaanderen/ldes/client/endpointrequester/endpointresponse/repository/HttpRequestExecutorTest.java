package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpointresponse.repository;

import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint.ApiKey;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint.Endpoint;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpointresponse.EndpointResponse;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpointresponse.EndpointResponseFactory;
import org.apache.http.HttpHeaders;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.web.HttpMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpRequestExecutorTest {

	@InjectMocks
	private HttpRequestExecutor httpRequestExecutor;

	@Mock
	private EndpointResponseFactory endpointResponseFactory;

	@Test
	void shouldReturnResponseContainingUrl_whenSuccess() throws Exception {
		InputStream inputStream = mock(InputStream.class);
		HttpURLConnection urlConnection = mock(HttpURLConnection.class);
		when(urlConnection.getInputStream()).thenReturn(inputStream);
		String contentType = "contentType";
		Endpoint endpoint = mock(Endpoint.class);
		when(endpoint.contentType()).thenReturn(contentType);
		when(endpoint.httpConnection()).thenReturn(urlConnection);
		when(endpoint.lang()).thenReturn(Lang.TURTLE);
		when(endpoint.getApiKey()).thenReturn(new ApiKey("header", "key"));

		EndpointResponse endpointResponse = new EndpointResponse(null);
		when(endpointResponseFactory.createResponse(inputStream, Lang.TURTLE)).thenReturn(endpointResponse);

		EndpointResponse result = httpRequestExecutor.getEndpointResponse(endpoint);

		verify(urlConnection).setRequestMethod(HttpMethod.METHOD_GET.method());
		verify(urlConnection).setRequestProperty(HttpHeaders.ACCEPT, contentType);
		verify(urlConnection).setRequestProperty("header", "key");
		assertEquals(endpointResponse, result);
	}

	@Test
	void shouldReturnEmptyResponse_whenFailure() throws IOException {
		Endpoint endpoint = mock(Endpoint.class);
		when(endpoint.httpConnection()).thenThrow(IOException.class);
		when(endpointResponseFactory.createEmptyResponse())
				.thenReturn(new EndpointResponse(ModelFactory.createDefaultModel()));

		EndpointResponse result = httpRequestExecutor.getEndpointResponse(endpoint);

		assertTrue(result.model().isEmpty());
	}

}
