package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint;

import org.apache.jena.riot.Lang;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * Contains the endpoint to connect to the server. This can be a collection,
 * view or subset.
 */
public class Endpoint {

	private final String url;
	private final Lang lang;
	private final ApiKey apiKey;

	public Endpoint(String url, Lang lang) {
		this(url, lang, ApiKey.empty());
	}

	public Endpoint(String url, Lang lang, ApiKey apiKey) {
		this.url = url;
		this.lang = lang;
		this.apiKey = apiKey;
	}

	public HttpURLConnection httpConnection() throws IOException {
		return (HttpURLConnection) new URL(url).openConnection();
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

	public ApiKey getApiKey() {
		return apiKey;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (Endpoint) obj;
		return Objects.equals(this.url, that.url) &&
				Objects.equals(this.lang, that.lang) &&
				Objects.equals(this.apiKey, that.apiKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, lang, apiKey);
	}

	@Override
	public String toString() {
		return "Endpoint[" +
				"apiKey=" + apiKey + ", " +
				"url=" + url + ", " +
				"lang=" + lang + ']';
	}

}
