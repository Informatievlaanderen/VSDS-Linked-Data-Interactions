package be.vlaanderen.informatievlaanderen.ldes.client.exceptions;

/**
 * An exception that is thrown when an I/O or HTTP exception occurs while
 * fetching fragments.
 */
public class FragmentFetcherException extends LdesException {

	/** Implements Serializable. */
	private static final long serialVersionUID = 4147040642407255794L;

	public FragmentFetcherException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
