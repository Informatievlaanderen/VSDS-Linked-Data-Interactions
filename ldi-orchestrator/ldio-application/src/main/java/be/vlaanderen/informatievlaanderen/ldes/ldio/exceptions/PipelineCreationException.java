package be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions;

public class PipelineCreationException extends RuntimeException {
	private final String pipeline;

	public PipelineCreationException(String pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public String getMessage() {
		return "Cannot create pipeline \"%s\". Pipeline with this name already exists".formatted(pipeline);
	}
}
