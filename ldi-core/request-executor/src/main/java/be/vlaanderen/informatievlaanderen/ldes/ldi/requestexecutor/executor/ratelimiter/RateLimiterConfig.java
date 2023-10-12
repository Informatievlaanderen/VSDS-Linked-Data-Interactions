package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;

public class RateLimiterConfig {

    public static RateLimiterConfig of() {
        return new RateLimiterConfig();
    }

    public RateLimiter getRateLimiter() {
        // TODO: 12/10/23 impl me
        return null;
    }

}
