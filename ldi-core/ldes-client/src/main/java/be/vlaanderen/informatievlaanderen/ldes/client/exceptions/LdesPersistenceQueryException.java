package be.vlaanderen.informatievlaanderen.ldes.client.exceptions;

public class LdesPersistenceQueryException extends LdesException {

	/** Implements Serializable. */
	private static final long serialVersionUID = -5060898267377648070L;

	public LdesPersistenceQueryException(String message) {
		super(message);
	}

	public LdesPersistenceQueryException(String message, Throwable t) {
		super(message, t);
	}
}
