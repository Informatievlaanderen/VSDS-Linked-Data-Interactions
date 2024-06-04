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

	/**
	 * Fetching the first relations of a (sub)set below an uri
	 *
	 * @param url  the starting uri
	 * @param lang the RDF format in which the response is received
	 * @return a list of the first relations below the starting uri
	 */
	public List<StartingTreeNode> getStartingTreeNodeRelations(String url, Lang lang) {
		final StartingNodeRequest startingNodeRequest = new StartingNodeRequest(url, lang, new RedirectHistory());
		return startingTreeNodeRelationsFinder
				.findAllStartingTreeNodes(startingNodeRequest).stream()
				.toList();
	}
}
