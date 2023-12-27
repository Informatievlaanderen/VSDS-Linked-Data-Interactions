package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects.EdcUrlProxy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdcRequestExecutorTest {

	@InjectMocks
	private EdcRequestExecutor edcRequestExecutor;

	@Mock
	private EdcUrlProxy urlProxy;

	@Mock
	private RequestExecutor requestExecutor;

	@Mock
	private TokenService tokenService;

	@Test
	void test_execute() {
		final var url = "http://example.org";
		when(urlProxy.proxy(url)).thenReturn(url);
		final var requestHeader = new RequestHeader("Authorization", "1234");
		when(tokenService.waitForTokenHeader()).thenReturn(requestHeader);

		final var request = new GetRequest(url, RequestHeaders.empty());
		final var edcRequest = new GetRequest(url, new RequestHeaders(List.of(requestHeader)));
		when(requestExecutor.execute(edcRequest))
				.thenReturn(new Response(request, List.of(), 403, null))
				.thenReturn(new Response(request, List.of(), 200, "body"));

		Response response = edcRequestExecutor.execute(request);

		verify(tokenService).invalidateToken();
		assertEquals(200, response.getHttpStatus());
		assertTrue(response.getBody().isPresent());
		assertEquals("body", response.getBody().get());
	}

}