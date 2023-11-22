package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TransferService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class LdioLdesClientConnector extends LdioLdesClient {
	public static final String NAME = "be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnector";
	private static final Logger log = LoggerFactory.getLogger(LdioLdesClientConnector.class);
	private final String pipelineName;
	private final TransferService transferService;
	private final TokenService tokenService;

	public LdioLdesClientConnector(String pipelineName, TransferService transferService, TokenService tokenService,
	                               RequestExecutor edcRequestExecutor, ComponentProperties properties,
	                               ComponentExecutor executor, StatePersistence statePersistence) {
		super(NAME, pipelineName, executor, edcRequestExecutor, properties, statePersistence);
		this.pipelineName = pipelineName;
		this.transferService = transferService;
		this.tokenService = tokenService;
	}

	public RouterFunction<ServerResponse> apiEndpoints() {
		return route(POST("/%s/token".formatted(pipelineName)),
				request -> {
					logIncomingRequest(request);
					return request.bodyToMono(String.class)
							.doOnNext(tokenService::updateToken)
							.flatMap(body -> ServerResponse.accepted().build());
				}).andRoute(POST("/%s/transfer".formatted(pipelineName)),
						request -> {
							logIncomingRequest(request);
							return request.bodyToMono(String.class)
									.flatMap(requestString -> {
										var response = transferService.startTransfer(requestString).getBody()
												.orElse("");
										return ServerResponse.accepted().body(Mono.just(response), String.class);
									});
						});
	}

	private void logIncomingRequest(ServerRequest request) {
		var httpMethod = HttpMethod.POST.name();
		var type = request.headers().contentType().map(MediaType::toString).orElse("(unknown)");
		long contentLength = request.headers().contentLength().orElse(0L);
		log.atDebug().log("{} /{} type: {} length: {}", httpMethod, pipelineName, type, contentLength);
	}

}
