package ldes.client.requestexecutor;

import org.apache.jena.riot.Lang;

import java.util.Objects;

/**
 * Contains the request details to connect to the server.
 */
public class Request {

	private final String url;
	private final Lang lang;

	public Request(String url, Lang lang) {
		this.url = url;
		this.lang = lang;
	}

	public String contentType() {
		return lang == null ? "" : lang.getContentType().getContentTypeStr();
	}

	public String url() {
		return url;
	}

	public Lang lang() {
		return lang;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (Request) obj;
		return Objects.equals(this.url, that.url) &&
				Objects.equals(this.lang, that.lang);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, lang);
	}

	@Override
	public String toString() {
		return "Endpoint[" +
				"url=" + url + ", " +
				"lang=" + lang + ']';
	}

}
