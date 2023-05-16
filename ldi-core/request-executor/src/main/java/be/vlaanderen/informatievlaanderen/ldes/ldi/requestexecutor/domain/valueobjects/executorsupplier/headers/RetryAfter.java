package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier.headers;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.utils.DateUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class RetryAfter {

    private final LocalDateTime retryAfterTimeStamp;

    public RetryAfter(LocalDateTime localDateTime) {
        this.retryAfterTimeStamp = localDateTime;
    }

    // TODO: 16/05/2023 test
    /**
     * Per <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Retry-After">specification</a>
     * this header can be a date or an integer.
     * @param retryHeader represents either a valid http date or an integer for milliseconds
     */
    public static RetryAfter from(String retryHeader) {
        if (NumberUtils.isParsable(retryHeader)) {
            return new RetryAfter(LocalDateTime.now().plusSeconds(Integer.parseInt(retryHeader)));
        } else {
            Date date = DateUtils.parseDate(retryHeader);
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return new RetryAfter(localDateTime);
        }
    }

    // TODO: 16/05/2023 test
    /**
     * Returns the number of millis until a retry can be done.
     * Return value is always positive.
     */
    public long getMillisUntilRetry() {
        LocalDateTime now = LocalDateTime.now();
        if (retryAfterTimeStamp.isAfter(now)) {
            // We take the absolute value to ensure we always return a positive value.
            // This is for the edge-case where the if check is true because of a 0.0000001 difference.
            return Math.abs(ChronoUnit.MILLIS.between(retryAfterTimeStamp, now));
        } else {
            return 1L;
        }
    }

}
