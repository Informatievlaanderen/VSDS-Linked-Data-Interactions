package be.vlaanderen.informatievlaanderen.ldes.client.exceptions;

public class LdesConfigurationException extends LdesException {

	/** Implements Serializable. */
	private static final long serialVersionUID = -5060898267377648070L;

	private final String message;

	public LdesConfigurationException(String message, String propertiesFile, Throwable t) {
		super(message, t);

		this.message = message + " (FILE: " + propertiesFile + ")";
	}

	@Override
	public String getMessage() {
		return message;
	}
}
