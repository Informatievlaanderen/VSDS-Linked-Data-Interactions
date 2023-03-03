package ldes.client.requestexecutor;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpRequestExecutorTest {

	@Test
	void test() throws IOException, InterruptedException {
		HttpClient httpClient = mock(HttpClient.class);
		HttpResponse<String> httpResponse = mock(HttpResponse.class);
		when(httpResponse.statusCode()).thenReturn(123);
		when(httpResponse.body()).thenReturn("body");
		when(httpResponse.headers()).thenReturn(HttpHeaders.of(Map.of(), (s, s2) -> false));
		HttpRequestExecutor httpRequestExecutor = new HttpRequestExecutor(httpClient);
		Request request = new Request("https://someUrl.be", new RequestHeaders(List.of()));
		when(httpClient.send(request.createHttpRequest(), HttpResponse.BodyHandlers.ofString()))
				.thenReturn(httpResponse);

		Response actualResponse = httpRequestExecutor.executeRequest(request);

		Response expectedResponse = new Response(123, HttpHeaders.of(Map.of(), (s, s2) -> false), "body");
		assertEquals(expectedResponse, actualResponse);
	}

}