package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import org.apache.commons.lang3.stream.Streams;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.stream.Stream;

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
		return arguments.containsFlag(DISABLE_RETRY);
	}

	public int getRetryLimit() {
		return arguments.getInteger(RETRY_LIMIT).orElse(DEFAULT_RETRY_LIMIT);
	}

	public List<Integer> getRetryStatuses() {
		return arguments.getValue(RETRY_STATUSES)
				.map(string -> string.split(","))
				.map(Streams::of)
				.orElseGet(Stream::of)
				.map(String::trim)
				.map(Integer::parseInt)
				.toList();
	}
}
