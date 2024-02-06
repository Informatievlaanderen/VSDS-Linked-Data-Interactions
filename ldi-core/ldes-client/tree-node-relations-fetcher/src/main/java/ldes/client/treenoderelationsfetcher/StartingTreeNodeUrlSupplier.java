package ldes.client.treenoderelationsfetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.startingtreenode.StartingTreeNodeFinder;
import ldes.client.startingtreenode.domain.valueobjects.RedirectHistory;
import ldes.client.startingtreenode.domain.valueobjects.StartingNodeRequest;
import ldes.client.startingtreenode.domain.valueobjects.StartingTreeNode;
import org.apache.jena.riot.Lang;

public class StartingTreeNodeUrlSupplier {
	private final StartingTreeNodeFinder startingTreeNodeFinder;

	public StartingTreeNodeUrlSupplier(RequestExecutor requestExecutor) {
		startingTreeNodeFinder = new StartingTreeNodeFinder(requestExecutor);
	}

	public String getStart(String url, Lang lang) {
		final StartingNodeRequest startingNodeRequest = new StartingNodeRequest(url, lang, new RedirectHistory());
		final StartingTreeNode startingTreeNode = startingTreeNodeFinder.determineStartingTreeNode(startingNodeRequest);
		return startingTreeNode.getUrl();
	}
}
