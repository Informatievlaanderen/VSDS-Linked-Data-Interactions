package be.vlaanderen.informatievlaanderen.ldes.poller;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@ExtendWith(MockitoExtension.class)
class HttpInputPollerTest {
	private WebTestClient client;

	private LdiAdapter adapter;

	private final String endpoint = "/endpoint";


	@BeforeEach
	void setUp() {
		adapter = Mockito.mock(LdiAdapter.class);
		ComponentExecutor executor = Mockito.mock(ComponentExecutor.class);

//		when(adapter.apply(any())).thenReturn(Stream.empty());

//		httpInputPoller = new HttpInputPoller(executor, adapter, endpoint);


	}

	RouterFunction<ServerResponse> getDataFromRequest() {
		return route(GET(endpoint), req -> ServerResponse.ok().body(Mono.just(List.of("el1", "el2")), List.class));
	}


	@Test
	void poll() {
		WebClient webClientMock = mock(WebClient.class);
		WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
		when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
		when(requestHeadersUriSpecMock.uri(endpoint)).thenReturn(requestHeadersSpecMock);
		when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
		when(responseSpecMock.bodyToMono(List.class)).thenReturn(Mono.just(List.of("el1", "el2")));



//		List fetchedData =  WebTestClient.bindToRouterFunction(getDataFromRequest())
//				.build()
//				.get()
//				.uri(endpoint)
//				.exchange()
//				.expectStatus().isOk()
//				.expectBody(List.class)
//				.returnResult()
//				.getResponseBody();

		StepVerifier.create(Mono.just(List.of("el1", "el2")))
				.expectNextMatches(list -> list.containsAll(List.of("el1", "el2")))
				.verifyComplete();


	}
}