package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class ParseToJsonException extends RuntimeException {
	private final String value;
	private final String cause;

	public ParseToJsonException(Exception e, String value) {
		this.cause = e.getMessage();
		this.value = value;
	}

	@Override
	public String getMessage() {
		return "Could not parse string to JSON. String with value:\n"
				+ value + "\nCause: " + cause;
	}
}
