package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception;

public class PipelineInitialisationException extends PipelineException {
	private final String pipelineName;

	public PipelineInitialisationException(String pipelineName, Throwable throwable) {
		super(throwable);
		this.pipelineName = pipelineName;

	}
	@Override
	public String getMessage() {
		return "Error while initialising pipeline \"%s\": %s".formatted(pipelineName, this.getCause().getMessage());
	}
}
