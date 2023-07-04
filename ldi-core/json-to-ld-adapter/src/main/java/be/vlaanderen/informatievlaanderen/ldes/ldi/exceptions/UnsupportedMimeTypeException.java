package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class UnsupportedMimeTypeException extends RuntimeException {
	private final String expected;
	private final String provided;

	public UnsupportedMimeTypeException(String expected, String provided) {
		super();
		this.expected = expected;
		this.provided = provided;
	}

	@Override
	public String getMessage() {
		return "Unsupported MIME type was provided: " + provided + ". Supported MIME type is: " + expected;
	}
}
