package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class RateLimiterConfig {
    private static final String RATE_LIMIT_PER_MINUTE_MIGRATION_WARNING = "'rate-limit.max-requests-per-minute' property is deprecated. Please consider migrating to the more generic properties 'rate-limit.limit' and 'rate-limit.period'";
    private static final String INVALID_PERIOD_ERROR = "Invalid config for the property 'rate-limiter.period': this must be a valid 8601 duration";
    private static final Logger log = LoggerFactory.getLogger(RateLimiterConfig.class);

	private final int limitForPeriod;
	private final Duration limitRefreshPeriod;
	private final Duration timeoutDuration;

	private RateLimiterConfig(int limitForPeriod, Duration limitRefreshPeriod, Duration timeoutDuration) {
		this.limitForPeriod = limitForPeriod;
		this.limitRefreshPeriod = limitRefreshPeriod;
		this.timeoutDuration = timeoutDuration;
	}

    public static RateLimiterConfig limitPerMinute(int maxRequestsPerMinute) {
        log.warn(RATE_LIMIT_PER_MINUTE_MIGRATION_WARNING);
        return new RateLimiterConfig(maxRequestsPerMinute, Duration.ofMinutes(1), Duration.ofMinutes(1));
    }

    public static RateLimiterConfig limitForPeriod(int limit, String period) {
        try {
            return new RateLimiterConfig(limit, Duration.parse(period), Duration.parse(period));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(INVALID_PERIOD_ERROR);
        }
    }

    public RateLimiter getRateLimiter() {
        return RateLimiterRegistry.of(
                        io.github.resilience4j.ratelimiter.RateLimiterConfig
                                .custom()
                                .limitForPeriod(limitForPeriod)
                                .limitRefreshPeriod(limitRefreshPeriod)
                                .timeoutDuration(timeoutDuration)
                                .build())
                .rateLimiter("rate-limit-http-requests");
    }

}
