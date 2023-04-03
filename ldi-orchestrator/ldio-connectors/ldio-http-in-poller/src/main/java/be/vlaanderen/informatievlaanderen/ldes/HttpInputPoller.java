package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.UnsuccesfullPollingException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class HttpInputPoller extends LdiInput {
	private final WebClient client;
	private final String endpoint;
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpInputPoller.class);

	public HttpInputPoller(ComponentExecutor executor, LdiAdapter adapter, String endpoint) {
		super(executor, adapter);
		this.endpoint = endpoint;
		this.client = WebClient.create(endpoint);
	}

	public Mono<String> handleResponse(ClientResponse response) {
		if(response.statusCode().is2xxSuccessful()) {
			var contentType = response.headers().contentType().orElseThrow().toString();
			return response.bodyToMono(String.class)
					.doOnNext(content -> getAdapter().apply(LdiAdapter.Content.of(content, contentType))
							.forEach(getExecutor()::transformLinkedData));
		} else {
			throw new UnsuccesfullPollingException(response.statusCode().value(), endpoint);
		}
	}

	public void poll() {
		client.get()
				.exchangeToMono(this::handleResponse)
				.subscribe();
	}

}
