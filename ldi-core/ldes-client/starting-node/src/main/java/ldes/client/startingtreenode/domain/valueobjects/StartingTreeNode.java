package ldes.client.startingtreenode.domain.valueobjects;

import org.apache.jena.rdf.model.Model;

/**
 * Contains the rootNode endpoint information to start the client.
 */
public class StartingTreeNode {
	private final String url;
	private final Model responseModel;

	public StartingTreeNode(String url, Model responseModel) {
		this.url = url;
		this.responseModel = responseModel;
	}

	public String getUrl() {
		return url;
	}

	public Model getResponseModel() {
		return responseModel;
	}

}
