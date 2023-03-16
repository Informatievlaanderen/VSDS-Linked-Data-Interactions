package ldes.client.requestexecutor.domain.valueobjects;

import java.util.Map;
import java.util.Optional;

public class Response {

	private final int httpStatus;
	private final Map<String, String> headers;
	private final String body;

	public Response(Map<String, String> headers, int httpStatus, String body) {
		this.httpStatus = httpStatus;
		this.headers = headers;
		this.body = body;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public boolean hasStatus(int status) {
		return getHttpStatus() == status;
	}

	public Optional<String> getBody() {
		return Optional.ofNullable(body);
	}

	public Optional<String> getValueOfHeader(final String key) {
		return Optional.ofNullable(headers.get(key));
	}

}
