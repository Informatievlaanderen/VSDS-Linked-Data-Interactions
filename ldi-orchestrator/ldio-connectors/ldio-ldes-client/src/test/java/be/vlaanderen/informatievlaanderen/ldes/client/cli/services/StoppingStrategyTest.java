package be.vlaanderen.informatievlaanderen.ldes.client.cli.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class StoppingStrategyTest {

	UnreachableEndpointStrategy unreachableEndpointStrategy = new StoppingStrategy("endpoint");

	@Test
	void when_EndpointIsNotReachable_handleUnreachableEndpoint_should_return_false() {
		assertFalse(unreachableEndpointStrategy.handleUnreachableEndpoint(),
				"StoppingStrategy.handleUnreachableEndpoint() should return false");
	}

}