package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class InvalidPollerConfigException extends RuntimeException {
	private final String field;
	private final String value;

	public InvalidPollerConfigException(String field, String value) {
		this.field = field;
		this.value = value;
	}

	@Override
	public String getMessage() {
		return "Invalid config for the ldio http in poller: " + field + " cannot have following value: " + value;
	}
}
