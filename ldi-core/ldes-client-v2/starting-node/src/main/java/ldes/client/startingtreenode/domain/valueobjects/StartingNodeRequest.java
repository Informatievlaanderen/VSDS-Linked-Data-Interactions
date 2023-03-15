package ldes.client.startingtreenode.domain.valueobjects;

import org.apache.jena.riot.Lang;

import java.util.Objects;

/**
 * Contains the endpoint to connect to the server. This can be a collection,
 * view or subset.
 */
public class StartingNodeRequest {

	private final String url;
	private final Lang lang;

	public StartingNodeRequest(String url, Lang lang) {
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

	public StartingNodeRequest createRedirectedEndpoint(final String location) {
		return new StartingNodeRequest(location, lang);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof StartingNodeRequest startingNodeRequest))
			return false;
		return Objects.equals(url, startingNodeRequest.url) && Objects.equals(lang, startingNodeRequest.lang);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, lang);
	}
}
