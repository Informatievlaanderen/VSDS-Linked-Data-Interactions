package ldes.client.treenodesupplier.domain.valueobject;

import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import org.apache.jena.riot.Lang;

import java.util.List;

public class LdesMetaData {
	private final List<String> startingNodeUrls;
	private final Lang lang;

	public LdesMetaData(List<String> startingNodeUrls, Lang lang) {
		this.startingNodeUrls = startingNodeUrls;
		this.lang = lang;
	}

	public List<String> getStartingNodeUrls() {
		return startingNodeUrls;
	}

	public String getStartingNodeUrl() {
		if (startingNodeUrls.isEmpty()) {
			throw new IllegalArgumentException("No starting nodes defined for LDES Client.");
		}
		return startingNodeUrls.get(0);
	}

	public TreeNodeRequest createRequest(String treeNodeUrl) {
		return new TreeNodeRequest(treeNodeUrl, lang, null);
	}

	public Lang getLang() {
		return lang;
	}
}
