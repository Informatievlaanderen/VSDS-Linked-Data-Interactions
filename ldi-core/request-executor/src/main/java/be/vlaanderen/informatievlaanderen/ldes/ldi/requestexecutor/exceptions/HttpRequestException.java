package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions;

public class HttpRequestException extends RuntimeException {
	public HttpRequestException(Exception e) {
		super(e);
	}
}
