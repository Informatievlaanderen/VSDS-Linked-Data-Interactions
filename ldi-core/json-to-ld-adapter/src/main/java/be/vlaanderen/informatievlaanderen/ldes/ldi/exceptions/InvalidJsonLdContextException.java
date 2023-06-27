package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class InvalidJsonLdContextException extends RuntimeException {

	/** Implements Serializable. */
	private static final long serialVersionUID = 3371021714635245249L;

	public InvalidJsonLdContextException(String message) {
		super(message);
	}
}
