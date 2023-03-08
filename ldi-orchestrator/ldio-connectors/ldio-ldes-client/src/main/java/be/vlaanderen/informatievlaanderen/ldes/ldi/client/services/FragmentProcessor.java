package be.vlaanderen.informatievlaanderen.ldes.ldi.client.services;

import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.UnparseableFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesService;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.lang.Thread.sleep;

public class FragmentProcessor {
	private final Logger logger = LoggerFactory.getLogger(FragmentProcessor.class);
	private static final Logger LOGGER = LoggerFactory.getLogger(FragmentProcessor.class);
	protected final LdesService ldesService;
	private final ComponentExecutor componentExecutor;
	private final long maxPollingInterval;
	private LocalDateTime nextExpire;

	public FragmentProcessor(LdesService ldesService, ComponentExecutor componentExecutor, long maxPollingInterval) {
		this.ldesService = ldesService;
		this.componentExecutor = componentExecutor;
		this.maxPollingInterval = maxPollingInterval;
		this.nextExpire = LocalDateTime.now().plusSeconds(maxPollingInterval);
	}

	public void processLdesFragments() {
		try {

			if (ldesService.hasFragmentsToProcess()) {
				LdesFragment fragment = ldesService.processNextFragment();
				LOGGER.info("Fragment {} has {} member(s)", fragment.getFragmentId(), fragment.getMembers().size());
				fragment.getMembers().forEach(member -> componentExecutor.transformLinkedData(member.getMemberModel()));
				changeNextExpire(fragment.getExpirationDate());

			} else if (nextExpire.isAfter(LocalDateTime.now())) {
				long sleepDuration = LocalDateTime.now().until(nextExpire, ChronoUnit.MILLIS);
				LOGGER.info("Waiting for next fragment to expire: {} milliseconds", sleepDuration);
				nextExpire = LocalDateTime.now().plusSeconds(maxPollingInterval);
				sleep(sleepDuration);
			}
		} catch (UnparseableFragmentException ex) {
			logger.error(ex.getMessage());
			throw ex;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void changeNextExpire(LocalDateTime expirationTime) {
		if (expirationTime != null && expirationTime.isBefore(nextExpire)) {
			nextExpire = expirationTime;
		}
	}
}
