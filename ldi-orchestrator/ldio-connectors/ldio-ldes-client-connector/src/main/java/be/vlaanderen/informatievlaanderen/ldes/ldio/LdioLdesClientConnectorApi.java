package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class LdioLdesClientConnectorApi {

    private static final Logger log = LoggerFactory.getLogger(LdioLdesClientConnectorApi.class);
    private final String pipelineName;
    private final TransferService transferService;
    private final TokenService tokenService;

    public LdioLdesClientConnectorApi(String pipelineName, TransferService transferService, TokenService tokenService) {
        this.pipelineName = pipelineName;
        this.transferService = transferService;
        this.tokenService = tokenService;
    }

    public RouterFunction<ServerResponse> endpoints() {
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
                            .doOnNext(transferService::startTransfer)
                            .flatMap(body -> ServerResponse.accepted().build());
                });
    }

    private void logIncomingRequest(ServerRequest request) {
        var type = request.headers().contentType().map(MediaType::toString).orElse("(unknown)");
        long contentLength = request.headers().contentLength().orElse(0L);
        log.debug("POST /%s type: %s length: %s".formatted(pipelineName, type, contentLength));
    }

}