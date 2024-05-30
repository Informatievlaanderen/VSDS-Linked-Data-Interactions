package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.util.Arrays;
import java.util.Objects;

public class PostRequest extends Request {

	public static final String METHOD_NAME = "POST";
	/**
	 * Representation of the request body as a byte array, this is for binary support purposes
	 */
	private final byte[] body;

	public PostRequest(String url, RequestHeaders requestHeaders, String body) {
		this(url, requestHeaders, body == null ? null : body.getBytes());
	}

	public PostRequest(String url, RequestHeaders requestHeaders, byte[] body) {
		super(url, requestHeaders);
		this.body = body;
	}

	@Override
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

	/**
	 * Converts the request body, which is a byte array, to a string. Mind that not the byte array cannot always to proper converted to a string
	 *
	 * @return the request body as a string
	 */
	public String getBodyAsString() {
		return new String(body);
	}

	public byte[] getBody() {
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
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		PostRequest that = (PostRequest) o;
		return Arrays.equals(body, that.body);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), Arrays.hashCode(body));
	}

}
