package be.vlaanderen.informatievlaanderen.ldes.client.cli.services;

import be.vlaanderen.informatievlaanderen.ldes.client.converters.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.UnparseableFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesService;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.lang.Thread.sleep;

public class FragmentProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FragmentProcessor.class);
	protected final LdesService ldesService;
	private final PrintStream printStream;
	private final Lang destinationFormat;
	private final long maxPollingInterval;
	private LocalDateTime nextExpire;

	public FragmentProcessor(LdesService ldesService, PrintStream printStream, Lang destinationFormat,
			long maxPollingInterval) {
		this.ldesService = ldesService;
		this.printStream = printStream;
		this.destinationFormat = destinationFormat;
		this.maxPollingInterval = maxPollingInterval;
		this.nextExpire = LocalDateTime.now().plusSeconds(maxPollingInterval);
	}

	public void processLdesFragments() {
		try {

			if (ldesService.hasFragmentsToProcess()) {
				LdesFragment fragment = ldesService.processNextFragment();
				LOGGER.info("Fragment {} has {} member(s)", fragment.getFragmentId(), fragment.getMembers().size());
				fragment.getMembers().forEach(member -> printStream
						.println(ModelConverter.convertModelToString(member.getMemberModel(), destinationFormat)));
				changeNextExpire(fragment.getExpirationDate());

			} else if (nextExpire.isAfter(LocalDateTime.now())) {
				long sleepDuration = LocalDateTime.now().until(nextExpire, ChronoUnit.MILLIS);
				LOGGER.info("Waiting for next fragment to expire: {} milliseconds", sleepDuration);
				nextExpire = LocalDateTime.now().plusSeconds(maxPollingInterval);
				sleep(sleepDuration);
			}
		} catch (UnparseableFragmentException ex) {
			printStream.println(ex.getMessage());
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
