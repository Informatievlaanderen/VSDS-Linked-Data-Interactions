package be.vlaanderen.informatievlaanderen.ldes.ldi.client.services;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class WaitingStrategyTest {

	UnreachableEndpointStrategy unreachableEndpointStrategy = new WaitingStrategy(2);

	@Test
	void test() {
		Awaitility.reset();
		await()
				.atLeast(1, TimeUnit.SECONDS)
				.atMost(3, TimeUnit.SECONDS)
				.untilAsserted(() -> unreachableEndpointStrategy.handleUnreachableEndpoint());
	}

	@Test
	void test2() {
		Awaitility.reset();
		ConditionFactory conditionFactory = await()
				.atMost(200, TimeUnit.MILLISECONDS);
		ConditionTimeoutException conditionTimeoutException = assertThrows(ConditionTimeoutException.class,
				() -> conditionFactory
						.untilAsserted(() -> unreachableEndpointStrategy.handleUnreachableEndpoint()));
		assertEquals(TimeoutException.class, conditionTimeoutException.getCause().getClass());
	}

}