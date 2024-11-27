package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception;

public abstract class PipelineException extends RuntimeException {
	public PipelineException() {
		super();
	}

	public PipelineException(Throwable throwable) {
		super(throwable);
	}
}
