package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class UnsuccesfulPollingException extends RuntimeException {

	final int httpStatusCode;
	final String endpoint;

	public UnsuccesfulPollingException(int httpStatusCode, String endpoint) {
		this.httpStatusCode = httpStatusCode;
		this.endpoint = endpoint;
	}

	@Override
	public String getMessage() {
		return "Error while polling endpoint: " + endpoint + " Response has status code: " + httpStatusCode;
	}
}
