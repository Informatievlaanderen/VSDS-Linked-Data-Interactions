package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@ExtendWith(MockitoExtension.class)
class HttpInputPollerTest {
	private WebTestClient client;

	private LdiAdapter adapter;

	private WebClient webClientMock;
	/*
	 * @Autowired
	 * private WebTestClient webTestClient;
	 */
	// private WebTestClient testClient;

	private final String endpoint = "/endpoint";

	// private HttpInputPoller

	@BeforeEach
	void setUp() {
		adapter = mock(LdiAdapter.class);
		ComponentExecutor executor = mock(ComponentExecutor.class);

		webClientMock = mock(WebClient.class);

		// when(adapter.apply(any())).thenReturn(Stream.empty());

		// httpInputPoller = new HttpInputPoller(executor, adapter, endpoint);

		WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpecMock = null;
		/*
		 * when(webClientMock.get())
		 * .thenReturn(requestHeadersUriSpecMock);
		 * when(requestHeadersUriMock.uri("/employee/{id}", employeeId))
		 * .thenReturn(requestHeadersSpecMock);
		 * when(requestHeadersMock.retrieve())
		 * .thenReturn(responseSpecMock);
		 * MockServerRequest responseMock = mock(MockServerRequest.class);
		 * when(responseMock.bodyToMono(String.class))
		 * .thenReturn(Mono.just("el1"));
		 */

		// testClient = WebTestClient
		// .bindToServer()
		// .baseUrl("http://localhost:8080")
		// .build();
		// RouterFunction<ServerResponse> function = RouterFunctions.route(
		// RequestPredicates.GET(""),
		// request -> ServerResponse.ok().body(Mono.just(List.of("el1", "el2")),
		// List.class)
		// );
		// WebTestClient.bindToRouterFunction(function).build();

	}

	RouterFunction<ServerResponse> getDataFromRequest() {
		return route(GET(endpoint), req -> ServerResponse.ok().body(Mono.just(List.of("el1", "el2")), List.class));
	}

	@Test
	void poll() {
		/*
		 * WebClient webClientMock = mock(WebClient.class);
		 * WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock =
		 * mock(WebClient.RequestHeadersUriSpec.class);
		 * WebClient.RequestHeadersSpec requestHeadersSpecMock =
		 * mock(WebClient.RequestHeadersSpec.class);
		 * WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
		 * when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
		 * when(requestHeadersUriSpecMock.uri(endpoint)).thenReturn(
		 * requestHeadersSpecMock);
		 * when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
		 * when(responseSpecMock.bodyToMono(List.class)).thenReturn(Mono.just(List.of(
		 * "el1", "el2")));
		 */

		// Object body =
		// testClient.get().uri("").exchange().expectStatus().isOk().expectBody();

		// List fetchedData = WebTestClient.bindToRouterFunction(getDataFromRequest())
		// .build()
		// .get()
		// .uri(endpoint)
		// .exchange()
		// .expectStatus().isOk()
		// .expectBody(List.class)
		// .returnResult()
		// .getResponseBody();

		/*
		 * StepVerifier.create(Mono.just(List.of("el1", "el2")))
		 * .expectNextMatches(list -> list.containsAll(List.of("el1", "el2")))
		 * .verifyComplete();
		 */

	}
}
