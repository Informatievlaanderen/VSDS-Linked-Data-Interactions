package be.vlaanderen.informatievlaanderen.ldes.ldio.exception;

public class PipelineDoesNotExistsException extends PipelineException {
	private final String pipeline;

	public PipelineDoesNotExistsException(String pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public String getMessage() {
		return "Cannot process pipeline \"%s\". Does not exist.".formatted(pipeline);
	}
}
