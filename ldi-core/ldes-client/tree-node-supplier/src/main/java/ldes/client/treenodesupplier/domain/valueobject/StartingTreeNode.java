package ldes.client.treenodesupplier.domain.valueobject;

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

	public Lang getLang() {
		return lang;
	}

}
