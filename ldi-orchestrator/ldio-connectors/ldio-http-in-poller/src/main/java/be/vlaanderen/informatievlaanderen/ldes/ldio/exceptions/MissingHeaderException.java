package be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions;

public class MissingHeaderException extends RuntimeException {
	private final int httpStatusCode;
	private final String endpoint;

	public MissingHeaderException(int httpStatusCode, String endpoint) {
		this.httpStatusCode = httpStatusCode;
		this.endpoint = endpoint;
	}

	@Override
	public String getMessage() {
		return String.format(
				"Expected to find header with key 'Content-Type', but was not found in response with status %s for request to following endpoint: %s",
				httpStatusCode, endpoint);
	}
}
