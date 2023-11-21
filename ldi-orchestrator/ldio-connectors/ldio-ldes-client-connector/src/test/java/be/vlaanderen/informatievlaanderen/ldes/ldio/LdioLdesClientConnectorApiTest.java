package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TransferService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.*;

class LdioLdesClientConnectorApiTest {

	private WebTestClient client;
	private final String endpoint = "endpoint";
	private TransferService transferService;
	private TokenService tokenService;

	@BeforeEach
	void setup() {
		transferService = mock(TransferService.class);
		tokenService = mock(TokenService.class);

		final RouterFunction<ServerResponse> routerFunction = new LdioLdesClientConnectorApi(endpoint, transferService,
				tokenService).endpoints();

		client = WebTestClient
				.bindToRouterFunction(routerFunction)
				.build();
	}

	@Test
	void testTokenEndpoint() {
		String content = "token";

		client.post()
				.uri("/%s/token".formatted(endpoint))
				.body(Mono.just(content), String.class)
				.exchange()
				.expectStatus()
				.isAccepted();

		verify(tokenService).updateToken(content);
		verify(transferService, times(0)).startTransfer(any());
	}

	@Test
	void testTransferEndpoint() {
		String content = "transfer";
		String transferResult = "transfer-result";
		when(transferService.startTransfer(content)).thenReturn(new Response(null, List.of(), 200, "transfer-result"));

		client.post()
				.uri("/%s/transfer".formatted(endpoint))
				.body(Mono.just(content), String.class)
				.exchange()
				.expectStatus()
				.isAccepted()
				.expectBody(String.class)
				.isEqualTo(transferResult);

		verify(transferService).startTransfer(content);
		verify(tokenService, times(0)).updateToken(any());
	}

}