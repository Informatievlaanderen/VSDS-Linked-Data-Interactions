package ldes.client.startingtreenode.domain.valueobjects;

import org.apache.jena.rdf.model.Model;

/**
 * Contains the rootNode endpoint information to start the client.
 */
public class StartingTreeNode {
	private final String url;

	public StartingTreeNode(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

}
