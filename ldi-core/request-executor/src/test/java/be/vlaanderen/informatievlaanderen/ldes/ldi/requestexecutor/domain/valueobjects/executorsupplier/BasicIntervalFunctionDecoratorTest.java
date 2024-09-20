package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.BasicIntervalFunctionDecorator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.core.functions.Either;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicIntervalFunctionDecoratorTest {

	@Mock
	private IntervalFunction intervalFunction;

	@InjectMocks
	private BasicIntervalFunctionDecorator basicIntervalFunctionDecorator;

	@Test
	void should_callIntervalFunction_when_EitherContainsNoResponse() {
		int attempt = 10;
		long interval = 25L;
		when(intervalFunction.apply(attempt)).thenReturn(interval);

		Long result = basicIntervalFunctionDecorator.apply(attempt, Either.left(null));

		assertThat(result).isEqualTo(interval);
	}

	@Test
	void should_callIntervalFunction_when_ResponseContainsNoRetryAfterHeader() {
		int attempt = 10;
		long interval = 50L;
		when(intervalFunction.apply(attempt)).thenReturn(interval);
		Response response = new Response(null, List.of(), HttpStatus.SC_SERVICE_UNAVAILABLE, "body");

		Long result = basicIntervalFunctionDecorator.apply(attempt, Either.right(response));

		assertThat(result).isEqualTo(interval);
	}

	@Test
	void should_returnMilliSecondsBasedOnRetryAfterHeader_when_retryAfterHeaderIsPresentWithSeconds() {
		BasicHeader basicHeader = new BasicHeader(HttpHeaders.RETRY_AFTER, "25");
		Response response = new Response(null, List.of(basicHeader), HttpStatus.SC_SERVICE_UNAVAILABLE, "body");

		Long result = basicIntervalFunctionDecorator.apply(5, Either.right(response));

		// we allow a margin of 5000ms for the code to be executed.
		assertThat(result)
				.isGreaterThan(20000L)
				.isLessThanOrEqualTo(25000L);
		verifyNoInteractions(intervalFunction);
	}

	@Test
	void should_returnMilliSecondsBasedOnRetryAfterHeader_when_retryAfterHeaderIsPresentWithDate() {
		Header basicHeader = new BasicHeader(HttpHeaders.RETRY_AFTER, getHttpDateString(25));
		Response response = new Response(null, List.of(basicHeader), HttpStatus.SC_SERVICE_UNAVAILABLE, "body");

		Long result = basicIntervalFunctionDecorator.apply(5, Either.right(response));

		// we allow a margin of 5000ms for the code to be executed.
		assertThat(result)
				.isGreaterThan(20000L)
				.isLessThanOrEqualTo(25000L);
		verifyNoInteractions(intervalFunction);
	}

	@SuppressWarnings("SameParameterValue")
	private String getHttpDateString(int secondsOffset) {
		// Format for valid http date format
		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Date
		return ZonedDateTime.now(ZoneOffset.UTC)
				.plusSeconds(secondsOffset)
				.format(RFC_1123_DATE_TIME);
	}

}
