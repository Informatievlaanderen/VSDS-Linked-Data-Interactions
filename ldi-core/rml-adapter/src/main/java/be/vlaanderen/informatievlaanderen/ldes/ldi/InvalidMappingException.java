package be.vlaanderen.informatievlaanderen.ldes.ldi;

public class InvalidMappingException extends RuntimeException {
	private final String mapping;
	public InvalidMappingException(String mapping) {
		this.mapping = mapping;
	}

	@Override
	public String getMessage() {
		return "Provided mapping is invalid: %s".formatted(mapping);
	}
}
