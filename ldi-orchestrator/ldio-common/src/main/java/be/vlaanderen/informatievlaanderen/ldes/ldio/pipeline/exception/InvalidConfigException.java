package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception;

public class InvalidConfigException extends PipelineException {
    private final String cause;

    public InvalidConfigException(String cause) {
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return "Invalid config: \"%s\" .".formatted(cause);
    }
}
