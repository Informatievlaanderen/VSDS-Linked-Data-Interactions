package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception;

public class PipelineParsingException extends PipelineException {
	private final String pipeline;

	public PipelineParsingException(String pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public String getMessage() {
		return "Pipeline \"%s\": Parsing error: Could not parse to OrchestratorConfig".formatted(pipeline);
	}
}
