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
	private final RedirectHistory redirectHistory;

	public StartingNodeRequest(String url, Lang lang, RedirectHistory redirectHistory) {
		this.url = url;
		this.lang = lang;
		this.redirectHistory = redirectHistory;
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
		RedirectHistory updatedRedirectHistory = redirectHistory.addStartingNodeRequest(this);
		return new StartingNodeRequest(location, lang, updatedRedirectHistory);
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
