package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.lowerCase;

// TODO: 10/05/2023 add testing
public class Response {

	private final Request request;
	private final int httpStatus;
	private final List<Header> headers;
	private final String body;

	public Response(Request request, List<Header> headers, int httpStatus, String body) {
		this.request = request;
		this.httpStatus = httpStatus;
		this.headers = headers;
		this.body = body;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	private boolean hasStatus(List<Integer> statusList) {
		return statusList.contains(getHttpStatus());
	}

	public boolean isOk() {
		return hasStatus(List.of(HttpStatus.SC_OK));
	}

	public boolean isRedirect() {
		final int permanentRedirect = 308; // not yet included in apache library
		return hasStatus(List.of(
				HttpStatus.SC_MOVED_PERMANENTLY,
				HttpStatus.SC_MOVED_TEMPORARILY,
				HttpStatus.SC_TEMPORARY_REDIRECT,
				permanentRedirect));
	}

	public boolean isNotModified() {
		return hasStatus(List.of(HttpStatus.SC_NOT_MODIFIED));
	}

	public Optional<String> getRedirectLocation() {
		return getFirstHeaderValue(HttpHeaders.LOCATION).map(location -> {
			if (location.startsWith("http://") || location.startsWith("https://")) {
				return location;
			}

			if (location.startsWith("/")) {
				URI uri = URI.create(request.getUrl());
				String baseUrl = uri.getScheme() + "://" + uri.getHost();
				return baseUrl + location;
			}

			URI uri = URI.create(request.getUrl());
			int lastIndexOf = uri.toString().lastIndexOf("/");
			return uri.toString().substring(0, lastIndexOf) + "/" + location;
		});
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
