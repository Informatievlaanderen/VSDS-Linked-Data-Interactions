package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.ratelimiter;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RateLimiterConfigTest {
    private static final int LIMIT = 500;

    @Test
    void test_LimitPerMinute() {
        final io.github.resilience4j.ratelimiter.RateLimiterConfig rateLimiterConfig = RateLimiterConfig.limitPerMinute(LIMIT).getRateLimiter().getRateLimiterConfig();

        assertThat(rateLimiterConfig.getLimitForPeriod()).isEqualTo(LIMIT);
        assertThat(rateLimiterConfig.getLimitRefreshPeriod()).isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    void given_ValidPeriodAndLimit_when_LimitForPeriod_then_ReturnRateLimiterConfig() {
        final String periodString = "PT1M";

        final io.github.resilience4j.ratelimiter.RateLimiterConfig rateLimiterConfig = RateLimiterConfig.limitForPeriod(LIMIT, periodString).getRateLimiter().getRateLimiterConfig();

        assertThat(rateLimiterConfig.getLimitForPeriod()).isEqualTo(LIMIT);
        assertThat(rateLimiterConfig.getLimitRefreshPeriod()).isEqualTo(Duration.parse(periodString));
        assertThat(rateLimiterConfig.getTimeoutDuration()).isEqualTo(Duration.parse(periodString));
    }

    @Test
    void given_InvalidPeriod_when_LimitForPeriod_then_ThrowException() {
        String periodString = "TP1J";

        assertThatThrownBy(() -> RateLimiterConfig.limitForPeriod(LIMIT, periodString))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid config for the property 'rate-limiter.period': this must be a valid 8601 duration");
    }
}