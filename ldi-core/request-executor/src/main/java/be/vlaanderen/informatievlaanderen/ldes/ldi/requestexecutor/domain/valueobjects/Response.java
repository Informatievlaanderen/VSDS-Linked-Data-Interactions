package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects;

import org.apache.http.Header;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.lowerCase;

public class Response {

	private final int httpStatus;
	private final List<Header> headers;
	private final String body;

	public Response(List<Header> headers, int httpStatus, String body) {
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

	public Optional<String> getFirstHeaderValue(final String key) {
		return headers.stream()
				.filter(header -> Objects.equals(lowerCase(header.getName()), lowerCase(key)))
				.map(Header::getValue).findFirst();
	}

}
