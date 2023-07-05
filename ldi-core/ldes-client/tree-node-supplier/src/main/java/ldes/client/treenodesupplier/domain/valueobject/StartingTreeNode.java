package ldes.client.treenodesupplier.domain.valueobject;

import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

public class StartingTreeNode {

	private final String startingNodeUrl;
	private final Lang lang;
	private final Model responseModel;

	public StartingTreeNode(String startingNodeUrl, Lang lang, Model responseModel) {
		this.startingNodeUrl = startingNodeUrl;
		this.lang = lang;
		this.responseModel = responseModel;
	}

	public String getStartingNodeUrl() {
		return startingNodeUrl;
	}

	public TreeNodeRequest createRequest(String treeNodeUrl) {
		return new TreeNodeRequest(treeNodeUrl, lang, null);
	}

	public Model getResponseModel() {
		return responseModel;
	}

}
