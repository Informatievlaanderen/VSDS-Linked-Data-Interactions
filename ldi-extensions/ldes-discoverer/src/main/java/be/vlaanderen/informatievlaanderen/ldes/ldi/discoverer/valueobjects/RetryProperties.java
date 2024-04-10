package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import org.springframework.boot.ApplicationArguments;

import java.util.List;

public class RetryProperties {
	private static final String DISABLE_RETRY = "disable-retry";
	private static final String RETRY_LIMIT = "retry-limit";
	private static final int DEFAULT_RETRY_LIMIT = 5;
	private static final String RETRY_STATUSES = "retry-statuses";

	private final Arguments arguments;

	public RetryProperties(ApplicationArguments arguments) {
		this.arguments = new Arguments(arguments);
	}

	public boolean isRetryingDisabled() {
		return arguments.containsKey(DISABLE_RETRY);
	}

	public int getRetryLimit() {
		return arguments.getInteger(RETRY_LIMIT).orElse(DEFAULT_RETRY_LIMIT);
	}

	public List<Integer> getRetryStatuses() {
		return arguments.getArgumentValues(RETRY_STATUSES).stream().map(String::trim).map(Integer::parseInt).toList();
	}
}
