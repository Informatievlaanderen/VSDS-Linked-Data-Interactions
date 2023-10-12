package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

import java.time.Duration;

// TODO: 12/10/23 test
public class RateLimiterConfig {

    private final int limitForPeriod;
    private final Duration limitRefreshPeriod;
    private final Duration timeoutDuration;

    public RateLimiterConfig(int limitForPeriod, Duration limitRefreshPeriod, Duration timeoutDuration) {
        this.limitForPeriod = limitForPeriod;
        this.limitRefreshPeriod = limitRefreshPeriod;
        this.timeoutDuration = timeoutDuration;
    }

    public static RateLimiterConfig limitPerMinute(int maxRequestsPerMinute) {
        return new RateLimiterConfig(maxRequestsPerMinute, Duration.ofMinutes(1), Duration.ofMinutes(1));
    }

    public RateLimiter getRateLimiter() {
        return RateLimiterRegistry.of(
                io.github.resilience4j.ratelimiter.RateLimiterConfig
                        .custom()
                        .limitForPeriod(limitForPeriod)
                        .limitRefreshPeriod(limitRefreshPeriod)
                        .timeoutDuration(timeoutDuration)
                        .build()
        ).rateLimiter("rate-limit-http-requests");
    }

}
