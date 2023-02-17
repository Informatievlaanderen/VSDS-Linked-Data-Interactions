package be.vlaanderen.informatievlaanderen.ldes.client.exceptions;

public class LdesPersistenceException extends LdesException {

	/** Implements Serializable. */
	private static final long serialVersionUID = -5060898267377648070L;

	public LdesPersistenceException(String message) {
		super(message);
	}

	public LdesPersistenceException(String message, Throwable t) {
		super(message, t);
	}
}
