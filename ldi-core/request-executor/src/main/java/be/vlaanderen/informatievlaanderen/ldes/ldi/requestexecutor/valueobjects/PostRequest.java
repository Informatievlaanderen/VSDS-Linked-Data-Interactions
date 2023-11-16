package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.util.Objects;

public class PostRequest extends Request {

	public static final String METHOD_NAME = "POST";
	private final String body;

	public PostRequest(String url, RequestHeaders requestHeaders, String body) {
		super(url, requestHeaders);
		this.body = body;
	}

	public String getMethod() {
		return METHOD_NAME;
	}

	@Override
	public Request with(String url) {
		return new PostRequest(url, requestHeaders, body);
	}

	@Override
	public Request with(RequestHeaders requestHeaders) {
		return new PostRequest(url, requestHeaders, body);
	}

	public String getBody() {
		return body;
	}

	public String getContentType() {
		return getRequestHeaders()
				.getFirst(HttpHeaders.CONTENT_TYPE)
				.map(RequestHeader::getValue)
				.orElse(ContentType.TEXT_PLAIN.getMimeType());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		PostRequest that = (PostRequest) o;
		return Objects.equals(body, that.body);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), body);
	}

}
