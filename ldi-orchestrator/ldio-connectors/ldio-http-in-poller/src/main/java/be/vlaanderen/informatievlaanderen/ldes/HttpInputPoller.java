package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class HttpInputPoller extends LdiInput {
	private final WebClient client;

	public HttpInputPoller(ComponentExecutor executor, LdiAdapter adapter, String endpoint) {
		super(executor, adapter);
		this.client = WebClient.create(endpoint);
	}

	public Mono<String> handleResponse(ClientResponse response) {
		var contentType = response.headers().contentType().orElseThrow().toString();
		return response.bodyToMono(String.class)
				.doOnNext(content -> getAdapter().apply(LdiAdapter.Content.of(content, contentType))
						.forEach(getExecutor()::transformLinkedData));
	}

	public void poll() {
		client.get()
				.exchangeToMono(this::handleResponse)
				.subscribe();
	}

}
