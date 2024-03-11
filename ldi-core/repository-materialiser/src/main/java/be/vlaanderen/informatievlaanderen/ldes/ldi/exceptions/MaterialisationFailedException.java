package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class MaterialisationFailedException extends RuntimeException {
	public MaterialisationFailedException(Exception e) {
		super(e);
	}

	@Override
	public String getMessage() {
		return "Materialisation to triples store failed: %s".formatted(getCause().getMessage());
	}
}
