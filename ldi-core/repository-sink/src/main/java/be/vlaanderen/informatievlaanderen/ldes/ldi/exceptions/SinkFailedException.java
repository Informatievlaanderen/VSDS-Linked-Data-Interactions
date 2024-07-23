package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class SinkFailedException extends RuntimeException {
	public SinkFailedException(Exception e) {
		super(e);
	}

	@Override
	public String getMessage() {
		return "Flushing to triples store failed: %s".formatted(getCause().getMessage());
	}
}
