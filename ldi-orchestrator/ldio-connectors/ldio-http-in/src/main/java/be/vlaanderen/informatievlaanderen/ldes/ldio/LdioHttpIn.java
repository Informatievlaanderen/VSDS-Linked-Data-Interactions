package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter.Content;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.NoSuchElementException;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class LdioHttpIn extends LdiInput {
	private final String endpoint;

	public LdioHttpIn(ComponentExecutor executor, LdiAdapter adapter, String endpoint) {
		super(executor, adapter);
		this.endpoint = endpoint;
	}

	public RouterFunction<ServerResponse> mapping() {
		return route(POST("/%s".formatted(endpoint)),
				req -> {
					String contentType = req.headers().contentType()
							.orElseThrow(() -> new NoSuchElementException("No Content-Type header found"))
							.toString();
					return req.bodyToMono(String.class)
							.doOnNext(content -> getAdapter().apply(Content.of(content, contentType))
									.forEach(getExecutor()::transformLinkedData))
							.flatMap(body -> ServerResponse.ok().build());
				});
	}

}
