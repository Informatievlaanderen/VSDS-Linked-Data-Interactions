package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class SerializationToJsonException extends RuntimeException {
	private final String value;

	public SerializationToJsonException(Exception e, String value) {
		super(e);
		this.value = value;
	}

	@Override
	public String getMessage() {
		return "Could not serialize object with value: " + value;
	}
}
