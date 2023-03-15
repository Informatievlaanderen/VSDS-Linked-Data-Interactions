package ldes.client.treenodesupplier;

import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.startingtreenode.StartingTreeNodeFinder;
import ldes.client.startingtreenode.domain.valueobjects.RedirectHistory;
import ldes.client.startingtreenode.domain.valueobjects.StartingNodeRequest;
import ldes.client.startingtreenode.domain.valueobjects.StartingTreeNode;
import ldes.client.treenodesupplier.domain.valueobject.Ldes;
import org.apache.jena.riot.Lang;

public class LdesProvider {

	private final RequestExecutor requestExecutor;

	public LdesProvider(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	public Ldes getLdes(String url, Lang lang) {
		StartingTreeNodeFinder startingTreeNodeFinder = new StartingTreeNodeFinder(requestExecutor);
		StartingTreeNode startingTreeNode = startingTreeNodeFinder
				.determineStartingTreeNode(new StartingNodeRequest(url, lang, new RedirectHistory()));
		return new Ldes(startingTreeNode.getUrl(), lang);
	}
}
