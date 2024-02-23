package be.vlaanderen.informatievlaanderen.ldes.ldio.exception;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.NAME_PATTERN;

public class InvalidPipelineNameException extends PipelineException {
	private final String pipeline;

	public InvalidPipelineNameException(String pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public String getMessage() {
		return "Invalid pipeline name \"%s\". Must fulfill regex %s.".formatted(pipeline, NAME_PATTERN);
	}
}
