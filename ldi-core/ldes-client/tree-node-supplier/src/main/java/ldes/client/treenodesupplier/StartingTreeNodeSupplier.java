package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.startingtreenode.StartingTreeNodeFinder;
import ldes.client.startingtreenode.domain.valueobjects.RedirectHistory;
import ldes.client.startingtreenode.domain.valueobjects.StartingNodeRequest;
import ldes.client.treenodesupplier.domain.valueobject.StartingTreeNode;
import org.apache.jena.riot.Lang;

public class StartingTreeNodeSupplier {

	private final RequestExecutor requestExecutor;

	public StartingTreeNodeSupplier(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	public StartingTreeNode getStart(String url, Lang lang) {
		var startingTreeNodeFinder = new StartingTreeNodeFinder(requestExecutor);
		var startingNodeRequest = new StartingNodeRequest(url, lang, new RedirectHistory());
		var startingTreeNode = startingTreeNodeFinder.determineStartingTreeNode(startingNodeRequest);
		return new StartingTreeNode(startingTreeNode.getUrl(), lang);
	}

}
