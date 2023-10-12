package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;

// TODO TVB: 12/10/23 test
public class RequestExecutorDecorator {

    private final RequestExecutor requestExecutor;
    private Retry retry;
    private RateLimiter rateLimiter;

    private RequestExecutorDecorator(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    public static RequestExecutorDecorator decorate(RequestExecutor requestExecutor) {
        return new RequestExecutorDecorator(requestExecutor);
    }

    public RequestExecutorDecorator with(Retry retry) {
        this.retry = retry;
        return this;
    }

    public RequestExecutorDecorator with(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
        return this;
    }

    public RequestExecutor get() {
        if (retry != null && rateLimiter != null) {
            return request ->
                    Decorators
                            .ofSupplier(() -> requestExecutor.execute(request))
                            .withRateLimiter(rateLimiter)
                            .withRetry(retry)
                            .get();
        }

        if (retry != null) {
            return request ->
                    Decorators
                            .ofSupplier(() -> requestExecutor.execute(request))
                            .withRetry(retry)
                            .get();
        }

        if (rateLimiter != null) {
            return request ->
                    Decorators
                            .ofSupplier(() -> requestExecutor.execute(request))
                            .withRateLimiter(rateLimiter)
                            .get();
        }

        return requestExecutor;
    }
}
