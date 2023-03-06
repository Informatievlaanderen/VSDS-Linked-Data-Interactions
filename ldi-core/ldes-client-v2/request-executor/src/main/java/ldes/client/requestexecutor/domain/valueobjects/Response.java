package ldes.client.requestexecutor.domain.valueobjects;

import org.apache.http.HttpHeaders;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
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

	public Optional<String> getBody() {
		return Optional.ofNullable(body);
	}

	public Optional<String> getValueOfHeader(final String key) {
		return Optional.ofNullable(headers.get(key));
	}

	public boolean isImmutable() {
		return getValueOfHeader(HttpHeaders.CACHE_CONTROL)
				.map(cacheControl -> cacheControl.contains("immutable"))
				.orElse(false);
	}

	public int getCacheMaxAge() {
		return Arrays.stream(
				getValueOfHeader(HttpHeaders.CACHE_CONTROL)
						.map(cacheControl -> cacheControl.split(","))
						.orElse(new String[0])
				).filter(x -> x.contains("max-age"))
				.map(x -> x.split("=")[1])
				.map(Integer::parseInt)
				.findFirst()
				.orElse(0);
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
