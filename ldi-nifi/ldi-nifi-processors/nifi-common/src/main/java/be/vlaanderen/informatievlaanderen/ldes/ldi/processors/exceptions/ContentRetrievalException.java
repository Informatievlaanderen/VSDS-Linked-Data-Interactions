package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.exceptions;

public class ContentRetrievalException extends RuntimeException {

	// Implements Serializable
	private static final long serialVersionUID = -4686385428310050899L;

	private final long id;

	public ContentRetrievalException(long id, Throwable cause) {
		super(cause);
		this.id = id;
	}

	@Override
	public String getMessage() {
		return "Content of Flowfile " + id + " cannot be retrieved";
	}
}
