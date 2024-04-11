package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import org.springframework.boot.ApplicationArguments;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class RateLimitProperties {
	private static final String ENABLE_RATE_LIMIT = "enable-rate-limit";
	private static final String RATE_LIMIT = "rate-limit";
	private static final int DEFAULT_RATE_LIMIT = 500;
	private static final String RATE_LIMIT_PERIOD = "rate-limit-period";
	private static final String DEFAULT_PERIOD = "PT1M";
	private final Arguments arguments;

	public RateLimitProperties(ApplicationArguments arguments) {
		this.arguments = new Arguments(arguments);
	}

	public boolean isRateLimitEnabled() {
		return arguments.containsFlag(ENABLE_RATE_LIMIT);
	}

	public int getRateLimit() {
		return arguments.getInteger(RATE_LIMIT).orElse(DEFAULT_RATE_LIMIT);
	}

	public Duration getRateLimitPeriod() {
		final String periodStringValue = arguments.getValue(RATE_LIMIT_PERIOD).orElse(DEFAULT_PERIOD);
		try {
			return Duration.parse(periodStringValue);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Illegal value for %s:%s".formatted(RATE_LIMIT_PERIOD, periodStringValue), e);
		}
	}

}
