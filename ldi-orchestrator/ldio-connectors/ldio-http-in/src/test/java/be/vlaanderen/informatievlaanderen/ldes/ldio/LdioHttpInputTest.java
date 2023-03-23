package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpInAutoConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LdioHttpInputTest {
	private WebTestClient client;
	private LdiAdapter adapter;
	private final String endpoint = "endpoint";

	@BeforeEach
	void setup() {
		adapter = Mockito.mock(LdiAdapter.class);
		ComponentExecutor executor = Mockito.mock(ComponentExecutor.class);

		when(adapter.apply(any())).thenReturn(Stream.empty());

		RouterFunction<?> routerFunction = (RouterFunction<?>) new LdioHttpInAutoConfig.LdioHttpInConfigurator()
				.configure(adapter, executor, new ComponentProperties(Map.of("pipeline.name", endpoint)));

		client = WebTestClient
				.bindToRouterFunction(routerFunction)
				.build();
	}

	@Test
	void testHttpEndpoint() {
		String content = "_:b0 <http://schema.org/name> \"Jane Doe\" .";
		String contentType = "application/n-quads";

		client.post()
				.uri("/%s".formatted(endpoint))
				.body(Mono.just(content), String.class)
				.header("Content-Type", contentType)
				.exchange()
				.expectStatus()
				.isNoContent();

		verify(adapter).apply(LdiAdapter.Content.of(content, contentType));
	}

}
