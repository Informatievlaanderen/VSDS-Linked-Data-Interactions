package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class ModelParseIOException extends RuntimeException {

	private final String model;
	private final String cause;

	public ModelParseIOException(String model, String cause) {
		this.model = model;
		this.cause = cause;
	}

	@Override
	public String getMessage() {
		return "Could not parse content to model. cause:\n" + cause + "\nContent:\n" + model;
	}
}
