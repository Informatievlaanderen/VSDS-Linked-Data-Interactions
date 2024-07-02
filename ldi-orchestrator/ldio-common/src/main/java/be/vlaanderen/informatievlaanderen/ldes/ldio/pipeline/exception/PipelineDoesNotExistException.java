package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception;

public class PipelineDoesNotExistException extends PipelineException {
	private final String pipeline;

	public PipelineDoesNotExistException(String pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public String getMessage() {
		return "Pipeline \"%s\": No pipeline like that exists".formatted(pipeline);
	}
}
