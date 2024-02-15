package ldes.client.treenoderelationsfetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.startingtreenode.StartingTreeNodeRelationsFinder;
import ldes.client.startingtreenode.domain.valueobjects.RedirectHistory;
import ldes.client.startingtreenode.domain.valueobjects.StartingNodeRequest;
import ldes.client.startingtreenode.domain.valueobjects.StartingTreeNode;
import org.apache.jena.riot.Lang;

import java.util.List;

public class StartingTreeNodeUrlSupplier {
	private final StartingTreeNodeRelationsFinder startingTreeNodeRelationsFinder;

	public StartingTreeNodeUrlSupplier(RequestExecutor requestExecutor) {
		startingTreeNodeRelationsFinder = new StartingTreeNodeRelationsFinder(requestExecutor);
	}

	public List<StartingTreeNode> getStartingTreeNodeRelations(String url, Lang lang) {
		final StartingNodeRequest startingNodeRequest = new StartingNodeRequest(url, lang, new RedirectHistory());
		return startingTreeNodeRelationsFinder
				.findAllStartingTreeNodes(startingNodeRequest).stream()
				.toList();
	}
}
