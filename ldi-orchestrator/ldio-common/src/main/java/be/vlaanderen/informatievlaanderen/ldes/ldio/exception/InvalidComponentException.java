package be.vlaanderen.informatievlaanderen.ldes.ldio.exception;

public class InvalidComponentException extends PipelineException {
	private final String pipeline;
	private final String component;

	public InvalidComponentException(String pipeline, String component) {
		this.pipeline = pipeline;
		this.component = component;
	}

	@Override
	public String getMessage() {
		return "Pipeline \"%s\": Invalid LDIO component in \"%s\". Please validate by consulting the documentation.".formatted(pipeline, component);
	}
}
