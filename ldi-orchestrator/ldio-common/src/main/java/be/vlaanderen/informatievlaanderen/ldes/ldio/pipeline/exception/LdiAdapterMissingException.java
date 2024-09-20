package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception;

public class LdiAdapterMissingException extends PipelineException {
	private final String pipelineName;
	private final String inputName;

	public LdiAdapterMissingException(String pipelineName, String inputName) {
		this.pipelineName = pipelineName;
		this.inputName = inputName;
	}

	@Override
	public String getMessage() {
		return "Pipeline \"%s\": Input: \"%s\": Missing LDI Adapter".formatted(pipelineName, inputName);
	}
}
