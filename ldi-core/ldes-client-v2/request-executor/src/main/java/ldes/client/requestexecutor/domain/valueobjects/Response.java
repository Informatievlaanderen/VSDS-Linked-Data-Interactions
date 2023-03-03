package ldes.client.requestexecutor.domain.valueobjects;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Objects;

public class Response {
	private final int httpStatus;

	private final HttpHeaders responseHeaders;
	private final String body;

	public Response(int httpStatus, HttpHeaders responseHeaders, String body) {
		this.httpStatus = httpStatus;
		this.responseHeaders = responseHeaders;
		this.body = body;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public String getBody() {
		return body;
	}

	public List<String> getValueOfHeader(final String key) {
		return responseHeaders.allValues(key);

	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Response response))
			return false;
		return httpStatus == response.httpStatus && Objects.equals(body, response.body);
	}

	@Override
	public int hashCode() {
		return Objects.hash(httpStatus, body);
	}
}
