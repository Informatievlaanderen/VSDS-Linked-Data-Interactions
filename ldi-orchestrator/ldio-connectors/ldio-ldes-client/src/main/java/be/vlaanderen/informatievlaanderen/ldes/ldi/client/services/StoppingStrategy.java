package be.vlaanderen.informatievlaanderen.ldes.ldi.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoppingStrategy implements UnreachableEndpointStrategy {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoppingStrategy.class);
	private final String endpoint;

	public StoppingStrategy(String endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public boolean handleUnreachableEndpoint() {
		LOGGER.info("endpoint {} not available. Stopping.", endpoint);
		return false;
	}
}
