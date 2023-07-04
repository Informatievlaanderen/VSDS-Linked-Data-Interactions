package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier.retry;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.HttpStatusRetryPredicate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpStatusRetryPredicateTest {

	@Test
	void should_ReturnTrue_when_ResponseIsNull() {
		assertTrue(new HttpStatusRetryPredicate(List.of()).test(null));
	}

	@Test
	void should_ReturnTrue_when_ResponseStatusIsGreaterOrEqualsThan500() {
		Response response500 = new Response(null, List.of(), 500, null);
		Response response502 = new Response(null, List.of(), 502, null);
		assertTrue(new HttpStatusRetryPredicate(List.of()).test(response500));
		assertTrue(new HttpStatusRetryPredicate(List.of()).test(response502));
	}

	@Test
	void should_ReturnTrue_when_ResponseStatusIsTooManyRequests() {
		Response response = new Response(null, List.of(), 429, null);
		assertTrue(new HttpStatusRetryPredicate(List.of()).test(response));
	}

	@Test
	void should_ReturnTrue_when_ResponseStatusIsIncludedInStatusesToRetry() {
		int customStatusThatShouldTriggerRetry = 418;
		Response response = new Response(null, List.of(), customStatusThatShouldTriggerRetry, null);
		assertTrue(new HttpStatusRetryPredicate(List.of(customStatusThatShouldTriggerRetry)).test(response));
	}

	@Test
	void should_ReturnFalse_when_StatusIsValid() {
		Response response = new Response(null, List.of(), 200, null);
		assertFalse(new HttpStatusRetryPredicate(List.of()).test(response));
	}
}