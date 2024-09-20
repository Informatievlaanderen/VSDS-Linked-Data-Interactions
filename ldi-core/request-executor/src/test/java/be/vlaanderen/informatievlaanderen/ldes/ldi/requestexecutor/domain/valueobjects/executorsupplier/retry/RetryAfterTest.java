package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier.retry;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.RetryAfter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RetryAfterTest {

	@Nested
	class From {
		@Test
		void should_parseNumber_when_inputIsNumber() {
			long millisUntilRetry = RetryAfter.from("25").getMillisUntilRetry();

			// allow 5s margin for code to run
			assertTrue(millisUntilRetry > 20000 && millisUntilRetry <= 25000);
		}

		@Test
		void should_parseDate_when_inputIsDate() {
			// Format for valid http date format
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Date
			String httpDate = ZonedDateTime.now(ZoneOffset.UTC)
					.plusSeconds(25)
					.format(RFC_1123_DATE_TIME);

			long millisUntilRetry = RetryAfter.from(httpDate).getMillisUntilRetry();

			// allow 5s margin for code to run
			assertTrue(millisUntilRetry > 20000 && millisUntilRetry <= 25000);
		}
	}

	@Nested
	class GetMillisUntilRetry {
		@Test
		void should_returnPositive_when_retryHeaderIsInThePast() {
			long millisUntilRetry = new RetryAfter(LocalDateTime.MIN).getMillisUntilRetry();

			assertEquals(1L, millisUntilRetry);
		}

		@Test
		void should_returnMillisBetweenNow_and_retryHeader() {
			long millisUntilRetry = new RetryAfter(LocalDateTime.now().plusSeconds(25)).getMillisUntilRetry();

			// allow 5s margin for code to run
			assertTrue(millisUntilRetry > 20000 && millisUntilRetry <= 25000);
		}
	}
}