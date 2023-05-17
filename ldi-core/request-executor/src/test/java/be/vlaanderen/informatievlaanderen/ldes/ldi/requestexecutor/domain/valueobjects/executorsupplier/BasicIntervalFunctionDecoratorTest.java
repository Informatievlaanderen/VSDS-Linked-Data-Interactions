package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier.retry.BasicIntervalFunctionDecorator;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

		assertEquals(interval, result);
	}

	@Test
	void should_callIntervalFunction_when_ResponseContainsNoRetryAfterHeader() {
		int attempt = 10;
		long interval = 50L;
		when(intervalFunction.apply(attempt)).thenReturn(interval);
		Response response = new Response(null, List.of(), HttpStatus.SC_SERVICE_UNAVAILABLE, null);

		Long result = basicIntervalFunctionDecorator.apply(attempt, Either.right(response));

		assertEquals(interval, result);
	}

	@Test
	void should_returnMilliSecondsBasedOnRetryAfterHeader_when_retryAfterHeaderIsPresentWithSeconds() {
		BasicHeader basicHeader = new BasicHeader(HttpHeaders.RETRY_AFTER, "25");
		Response response = new Response(null, List.of(basicHeader), HttpStatus.SC_SERVICE_UNAVAILABLE, null);

		Long result = basicIntervalFunctionDecorator.apply(5, Either.right(response));

		// we allow a margin of 1000ms for the code to be executed.
		assertTrue(result > 24000 && result <= 25000);
		verifyNoInteractions(intervalFunction);
	}

	@Test
	void should_returnMilliSecondsBasedOnRetryAfterHeader_when_retryAfterHeaderIsPresentWithDate() {
		Header basicHeader = new BasicHeader(HttpHeaders.RETRY_AFTER, getHttpDateString(25));
		Response response = new Response(null, List.of(basicHeader), HttpStatus.SC_SERVICE_UNAVAILABLE, null);

		Long result = basicIntervalFunctionDecorator.apply(5, Either.right(response));

		// we allow a margin of 1000ms for the code to be executed.
		assertTrue(result > 24000 && result <= 25000);
		verifyNoInteractions(intervalFunction);
	}

	@SuppressWarnings("SameParameterValue")
	private String getHttpDateString(int secondsOffset) {
		// Format for valid http date format
		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Date
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, d MMM yyyy HH:mm:ss 'GMT'");

		return LocalDateTime.now(ZoneOffset.UTC).plusSeconds(secondsOffset).format(formatter);
	}

}