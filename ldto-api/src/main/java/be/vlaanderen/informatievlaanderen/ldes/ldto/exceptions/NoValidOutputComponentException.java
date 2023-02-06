package be.vlaanderen.informatievlaanderen.ldes.ldto.exceptions;

public class NoValidOutputComponentException extends RuntimeException {
	private final String outputProcessorName;

	public NoValidOutputComponentException(String outputProcessorName) {
		super();
		this.outputProcessorName = outputProcessorName;
	}

	@Override
	public String getMessage() {
		return "No valid output component was found for name " + outputProcessorName;
	}
}
