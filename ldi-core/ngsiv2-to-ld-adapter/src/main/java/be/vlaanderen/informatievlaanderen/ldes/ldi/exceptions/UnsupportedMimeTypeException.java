package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class UnsupportedMimeTypeException extends RuntimeException {
	private final String expected;
	private final String requested;

	public UnsupportedMimeTypeException(String expected, String requested) {
		super();
		this.expected = expected;
		this.requested = requested;
	}

	@Override
	public String getMessage() {
		return "Unsupported MIME type was requested: " + requested + ". Supported MIME type is: " + expected;
	}
}
