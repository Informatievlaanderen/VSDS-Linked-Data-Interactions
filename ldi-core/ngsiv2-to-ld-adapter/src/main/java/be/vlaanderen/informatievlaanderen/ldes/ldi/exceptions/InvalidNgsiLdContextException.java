package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class InvalidNgsiLdContextException extends RuntimeException {

	/** Implements Serializable. */
	private static final long serialVersionUID = 3371021714635245249L;

	public InvalidNgsiLdContextException(String message) {
		super(message);
	}
}
