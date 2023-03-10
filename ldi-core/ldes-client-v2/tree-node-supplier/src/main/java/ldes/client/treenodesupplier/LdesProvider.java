package ldes.client.treenodesupplier;

import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.startingtreenode.StartingTreeNodeFinder;
import ldes.client.startingtreenode.domain.valueobjects.Endpoint;
import ldes.client.startingtreenode.domain.valueobjects.TreeNode;
import ldes.client.treenodesupplier.domain.valueobject.Ldes;
import ldes.client.treenodesupplier.exception.NoStartingNodeException;
import org.apache.jena.riot.Lang;

import java.util.Optional;

public class LdesProvider {

	private final RequestExecutor requestExecutor;

	public LdesProvider(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	public Ldes getLdes(String url, Lang lang) {
		StartingTreeNodeFinder startingTreeNodeFinder = new StartingTreeNodeFinder(requestExecutor);
		Optional<ldes.client.startingtreenode.domain.valueobjects.TreeNode> startingTreeNode = startingTreeNodeFinder
				.determineStartingTreeNode(new Endpoint(url, lang));
		TreeNode treeNode = startingTreeNode.orElseThrow(() -> new NoStartingNodeException(url));
		return new Ldes(treeNode.getUrl(), lang);
	}
}
