package be.vlaanderen.informatievlaanderen.ldes.ldio.exception;

public class PipelineAlreadyExistsException extends PipelineException {
	private final String pipeline;

	public PipelineAlreadyExistsException(String pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public String getMessage() {
		return "Cannot create pipeline \"%s\". Pipeline with this name already exists.".formatted(pipeline);
	}
}
