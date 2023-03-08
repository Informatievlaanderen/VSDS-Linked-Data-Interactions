package be.vlaanderen.informatievlaanderen.ldes.ldi.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitingStrategy implements UnreachableEndpointStrategy {

	private final long endpointPollingInterval;
	private static final Logger LOGGER = LoggerFactory.getLogger(WaitingStrategy.class);

	public WaitingStrategy(long endpointPollingInterval) {
		this.endpointPollingInterval = endpointPollingInterval;
	}

	@Override
	public boolean handleUnreachableEndpoint() {
		waitUntilEndpointBecomesAvailable();
		return true;
	}

	private void waitUntilEndpointBecomesAvailable() {
		try {
			Thread.sleep(endpointPollingInterval * 1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.error("Interrupted thread", e);
		}
	}
}
