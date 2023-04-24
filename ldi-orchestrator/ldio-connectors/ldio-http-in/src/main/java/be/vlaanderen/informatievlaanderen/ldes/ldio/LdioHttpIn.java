package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter.Content;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDataTransformEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.NoSuchElementException;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class LdioHttpIn extends LdiInput {
	private final ApplicationEventPublisher applicationEventPublisher;
	private final String endpoint;

	public LdioHttpIn(ComponentExecutor executor, LdiAdapter adapter,
			ApplicationEventPublisher applicationEventPublisher, String endpoint) {
		super(executor, adapter);
		this.applicationEventPublisher = applicationEventPublisher;
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
									.forEach(model -> {
										applicationEventPublisher.publishEvent(new PipelineDataTransformEvent("name", model));
										getExecutor().transformLinkedData(model);
									}))
							.flatMap(body -> ServerResponse.ok().build());
				});
	}

}
