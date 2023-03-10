package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class DeserializationFromJsonException extends RuntimeException {
	private final String value;

	public DeserializationFromJsonException(Exception e, String value) {
		super(e);
		this.value = value;
	}

	@Override
	public String getMessage() {
		return "Could not deserialize string to LinkedDataModel or array of LinkedDataModel. String with value: "
				+ value;
	}
}
