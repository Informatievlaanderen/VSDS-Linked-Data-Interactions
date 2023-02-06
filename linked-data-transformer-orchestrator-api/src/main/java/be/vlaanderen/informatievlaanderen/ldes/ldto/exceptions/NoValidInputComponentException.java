package be.vlaanderen.informatievlaanderen.ldes.ldto.exceptions;

public class NoValidInputComponentException extends RuntimeException {
	private final String inputProcessorName;

	public NoValidInputComponentException(String inputProcessorName) {
		super();
		this.inputProcessorName = inputProcessorName;
	}

	@Override
	public String getMessage() {
		return "No valid input component was found for name " + inputProcessorName;
	}
}
