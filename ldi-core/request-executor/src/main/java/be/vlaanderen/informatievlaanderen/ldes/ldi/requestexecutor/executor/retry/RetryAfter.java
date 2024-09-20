package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry;

import org.apache.commons.lang3.math.NumberUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class RetryAfter {

	private final LocalDateTime retryAfterTimeStamp;

	public RetryAfter(LocalDateTime retryAfterTimeStamp) {
		this.retryAfterTimeStamp = retryAfterTimeStamp;
	}

	/**
	 * Per <a href=
	 * "https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Retry-After">specification</a>
	 * this header can be a date or an integer.
	 *
	 * @param retryHeader represents either a valid http date or an integer for milliseconds
	 */
	public static RetryAfter from(String retryHeader) {
		if (NumberUtils.isParsable(retryHeader)) {
			return new RetryAfter(LocalDateTime.now().plusSeconds(Integer.parseInt(retryHeader)));
		} else {
			Instant instant = ZonedDateTime.parse(retryHeader, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
			LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
			return new RetryAfter(localDateTime);
		}
	}

	/**
	 * @return a long that represents the millis until a retry can be done, is always positive.
	 */
	public long getMillisUntilRetry() {
		LocalDateTime now = LocalDateTime.now();
		if (retryAfterTimeStamp.isAfter(now)) {
			// We take the absolute value to ensure we always return a positive value.
			// This is for the edge-case where the if check is true because of a 0.0000001
			// difference.
			return Math.abs(ChronoUnit.MILLIS.between(retryAfterTimeStamp, now));
		} else {
			return 1L;
		}
	}

}
