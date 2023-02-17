package be.vlaanderen.informatievlaanderen.ldes.client.exceptions;

public class UnparseableFragmentException extends RuntimeException {

	/** Implements Serializable. */
	private static final long serialVersionUID = 2959837411139399356L;

	public UnparseableFragmentException(String message, Throwable cause) {
		super("LDES Client: " + message, cause);
	}
}
