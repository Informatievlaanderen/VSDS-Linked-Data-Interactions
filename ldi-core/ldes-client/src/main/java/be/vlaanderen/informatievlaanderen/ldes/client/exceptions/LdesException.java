package be.vlaanderen.informatievlaanderen.ldes.client.exceptions;

public class LdesException extends RuntimeException {

	/** Implements Serializable. */
	private static final long serialVersionUID = -9038571924019903006L;

	public LdesException(String message) {
		super(message);
	}

	public LdesException(String message, Throwable t) {
		super(message, t);
	}
}
