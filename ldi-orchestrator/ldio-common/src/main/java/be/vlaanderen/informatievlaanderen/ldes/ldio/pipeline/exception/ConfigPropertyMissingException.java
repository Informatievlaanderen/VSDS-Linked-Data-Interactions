package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception;

public class ConfigPropertyMissingException extends PipelineException {
	private final String pipeline;

	private final String component;
	private final String key;

	public ConfigPropertyMissingException(String pipeline, String component, String key) {
		this.pipeline = pipeline;
		this.component = component;
		this.key = key;
	}

	@Override
	public String getMessage() {
		return "Pipeline \"%s\": \"%s\" : Missing value for property \"%s\" .".formatted(pipeline, component, key);
	}
}
