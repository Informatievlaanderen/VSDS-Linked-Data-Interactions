package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.springframework.web.reactive.function.client.WebClient;

public class HttpInputPoller extends LdiInput {
	private final WebClient client;

	public HttpInputPoller(ComponentExecutor executor, LdiAdapter adapter, WebClient client) {
		super(executor, adapter);
		this.client = client;
	}

	public void poll() {
//		client.get().retrieve().bodyToMono(String.class);
		client.get()
				.exchangeToMono(response -> {
					var contentType = response.headers().contentType().orElseThrow().toString();
					return response.bodyToMono(String.class)
							.doOnNext(content -> getAdapter().apply(LdiAdapter.Content.of(content, contentType))
									.forEach(getExecutor()::transformLinkedData));
				});
	}

}
