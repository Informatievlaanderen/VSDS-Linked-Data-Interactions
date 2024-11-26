package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception;

public class PipelineInitialisationException extends PipelineException {
	private final String pipelineName;
	private final Exception exception;

	public PipelineInitialisationException(String pipelineName, Exception exception) {
		this.pipelineName = pipelineName;
		this.exception = exception;
	}

	@Override
	public String getMessage() {
		return "Error while initialising pipeline \"%s\": %s".formatted(pipelineName, exception.getMessage());
	}
}
