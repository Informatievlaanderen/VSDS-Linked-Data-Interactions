package ldes.client.treenodesupplier.domain.valueobject;

import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import org.apache.jena.riot.Lang;

public class StartingTreeNode {

	private final String startingNodeUrl;
	private final Lang lang;

	public StartingTreeNode(String startingNodeUrl, Lang lang) {
		this.startingNodeUrl = startingNodeUrl;
		this.lang = lang;
	}

	public String getStartingNodeUrl() {
		return startingNodeUrl;
	}

	public TreeNodeRequest createRequest(String treeNodeUrl) {
		return new TreeNodeRequest(treeNodeUrl, lang, null);
	}
}
