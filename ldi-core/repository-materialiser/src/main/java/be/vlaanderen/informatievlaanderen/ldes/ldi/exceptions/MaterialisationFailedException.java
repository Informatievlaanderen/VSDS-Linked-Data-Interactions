package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class MaterialisationFailedException extends RuntimeException {
	public MaterialisationFailedException(Exception e) {
		super(e);
	}
}
