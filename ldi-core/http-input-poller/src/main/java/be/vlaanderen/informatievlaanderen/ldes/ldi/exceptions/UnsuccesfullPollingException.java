package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class UnsuccesfullPollingException extends RuntimeException {

    final int httpStatusCode;
    final String endpoint;


    public UnsuccesfullPollingException(int httpStatusCode, String endpoint) {
        this.httpStatusCode = httpStatusCode;
        this.endpoint = endpoint;
    }

    @Override
    public String getMessage() {
        return "Error while polling endpoint: " + endpoint + " Response has status code: " + httpStatusCode;
    }
}
