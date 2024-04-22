package be.vlaanderen.informatievlaanderen.ldes.ldio.exception;

public class InvalidConfigException extends RuntimeException {
    private final String cause;

    public InvalidConfigException(String cause) {
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return "Invalid config: \"%s\" .".formatted(cause);
    }
}
