package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.NoSuchElementException;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class LdioHttpIn extends LdioInput {
	public final static String NAME = "be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn";
	private static final Logger log = LoggerFactory.getLogger(LdioHttpIn.class);
	private final String endpoint;

	public LdioHttpIn(String pipelineName, ComponentExecutor executor, LdiAdapter adapter) {
		super(NAME, pipelineName, executor, adapter);
		this.endpoint = pipelineName;
	}

	public RouterFunction<ServerResponse> mapping() {
		return route(POST("/%s".formatted(endpoint)),
				req -> {
					String contentType = req.headers().contentType()
							.orElseThrow(() -> new NoSuchElementException("No Content-Type header found"))
							.toString();

					logIncomingRequest(req);

					return req.bodyToMono(String.class)
							.doOnNext(content -> processInput(content, contentType))
							.flatMap(body -> ServerResponse.accepted().build());
				});
	}

	private void logIncomingRequest(ServerRequest request) {
		var httpMethod = HttpMethod.POST.name();
		var type = request.headers().contentType().map(MediaType::toString).orElse("(unknown)");
		long contentLength = request.headers().contentLength().orElse(0L);
		log.atDebug().log("{} /{} type: {} length: {}", httpMethod, endpoint, type, contentLength);
	}

}
