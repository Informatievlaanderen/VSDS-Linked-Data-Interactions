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

	/**
	 * @param maxRequestsPerMinute the maximum amount of request that may be performed during the time span of 1 minute
	 * @deprecated replaced by {@link #limitForPeriod} which provides a more flexible way of configuring the RateLimit
	 */
	@Deprecated(forRemoval = true)
	public static RateLimiterConfig limitPerMinute(int maxRequestsPerMinute) {
		log.warn(RATE_LIMIT_PER_MINUTE_MIGRATION_WARNING);
		return new RateLimiterConfig(maxRequestsPerMinute, Duration.ofMinutes(1), Duration.ofMinutes(1));
	}

	/**
	 * @param limit  the maximum number of request that may be performed in the specified
	 * @param period string presentation of the ISO-8601 duration wherein the maximum number of requests may be performed
	 * @see #limitForPeriod(int, Duration)
	 * @see #limitPerMinute
	 */
	public static RateLimiterConfig limitForPeriod(int limit, String period) {
		try {
			return limitForPeriod(limit, Duration.parse(period));
		} catch (DateTimeParseException exception) {
			throw new IllegalArgumentException(INVALID_PERIOD_ERROR);
		}
	}

	/**
	 * @param limit  the maximum number of request that may be performed in the specified
	 * @param period {@link java.time.Duration} instance of the duration wherein the maximum number of request may be performed
	 * @see #limitForPeriod(int, String)
	 * @see #limitPerMinute
	 */
	public static RateLimiterConfig limitForPeriod(int limit, Duration period) {
		return new RateLimiterConfig(limit, period, period);
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
