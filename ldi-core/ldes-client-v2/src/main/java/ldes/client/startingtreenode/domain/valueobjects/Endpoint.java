package ldes.client.startingtreenode.domain.valueobjects;

import org.apache.jena.riot.Lang;

import java.util.Objects;

/**
 * Contains the endpoint to connect to the server. This can be a collection,
 * view or subset.
 */
public class Endpoint {

	private final String url;
	private final Lang lang;

	public Endpoint(String url, Lang lang) {
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

	public Endpoint createRedirectedEndpoint(final String location){
		return new Endpoint(location, lang);
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Endpoint)) return false;
		Endpoint endpoint = (Endpoint) o;
		return Objects.equals(url, endpoint.url) && Objects.equals(lang, endpoint.lang);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, lang);
	}
}
