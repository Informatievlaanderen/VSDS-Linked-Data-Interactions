package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception;

public abstract class PipelineException extends RuntimeException {
	protected PipelineException() {
		super();
	}

	protected PipelineException(Throwable throwable) {
		super(throwable);
	}
}
