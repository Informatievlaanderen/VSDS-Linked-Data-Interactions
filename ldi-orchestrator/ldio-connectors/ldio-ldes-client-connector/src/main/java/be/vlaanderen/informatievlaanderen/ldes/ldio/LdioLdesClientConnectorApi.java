package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static sun.util.locale.provider.LocaleProviderAdapter.getAdapter;

public class LdioLdesClientConnectorApi {

    private static final Logger log = LoggerFactory.getLogger(LdioLdesClientConnectorApi.class);
    private final String pipelineName;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public LdioLdesClientConnectorApi(ComponentExecutor executor, LdiAdapter adapter, String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public RouterFunction<ServerResponse> mapping() {
        return route(POST("/%s/token".formatted(pipelineName)),
                req -> {
                    String contentType = req.headers().contentType()
                            .orElseThrow(() -> new NoSuchElementException("No Content-Type header found"))
                            .toString();

                    String type = req.headers().contentType().map(MediaType::toString).orElse("(unknown)");
                    log.info("POST " + "/%s".formatted(pipelineName) + " type:" + type + " length:"
                            + req.headers().contentLength().orElse(0L));

                    return req.bodyToMono(String.class)
                            .doOnNext(content -> executorService
                                    .execute(() -> getAdapter().apply(LdiAdapter.Content.of(content, contentType))
                                            .forEach(getExecutor()::transformLinkedData)))
                            .flatMap(body -> ServerResponse.accepted().build());
                }).andRoute(POST("/%s".formatted("tom")),
                req -> {
                    String contentType = req.headers().contentType()
                            .orElseThrow(() -> new NoSuchElementException("No Content-Type header found"))
                            .toString();

                    String type = req.headers().contentType().map(MediaType::toString).orElse("(unknown)");
                    log.info("POST " + "/%s".formatted(pipelineName) + " type:" + type + " length:"
                            + req.headers().contentLength().orElse(0L));

                    return req.bodyToMono(String.class)
                            .doOnNext(content -> executorService
                                    .execute(() -> getAdapter().apply(LdiAdapter.Content.of(content, contentType))
                                            .forEach(getExecutor()::transformLinkedData)))
                            .flatMap(body -> ServerResponse.accepted().build());
                });
    }

    public RouterFunction<ServerResponse> tom() {
        return route(POST("/%s".formatted("tom")),
                req -> {
                    String contentType = req.headers().contentType()
                            .orElseThrow(() -> new NoSuchElementException("No Content-Type header found"))
                            .toString();

                    String type = req.headers().contentType().map(MediaType::toString).orElse("(unknown)");
                    log.info("POST " + "/%s".formatted(pipelineName) + " type:" + type + " length:"
                            + req.headers().contentLength().orElse(0L));

                    return req.bodyToMono(String.class)
                            .doOnNext(content -> executorService
                                    .execute(() -> getAdapter().apply(LdiAdapter.Content.of(content, contentType))
                                            .forEach(getExecutor()::transformLinkedData)))
                            .flatMap(body -> ServerResponse.accepted().build());
                });
    }

}
