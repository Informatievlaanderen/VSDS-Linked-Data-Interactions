package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.TreeRelationsFetcher;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeNodeDiscoverer {
	private static final Logger log = LoggerFactory.getLogger(TreeNodeDiscoverer.class);
	private final TreeRelationsFetcher treeNodeFetcher;
	private final LdesMetaData ldesMetaData;
	private final RequestExecutor requestExecutor;
	private final Map<String, List<String>> relations = new HashMap<>();

	public TreeNodeDiscoverer(LdesMetaData ldesMetaData, RequestExecutor requestExecutor) {
		this.ldesMetaData = ldesMetaData;
		this.requestExecutor = requestExecutor;
		this.treeNodeFetcher = new TreeRelationsFetcher(requestExecutor);

	}

	public Map<String, List<String>> discoverNodes() {
		String startingNodeUrl = getStartingNodeUrl();
		List<String> fetchedRelations = fetchRelations(startingNodeUrl);
		while (!fetchedRelations.isEmpty()) {
			fetchedRelations = fetchedRelations.parallelStream().flatMap(relation -> fetchRelations(relation).stream()).toList();
		}

		return relations;
	}

	private String getStartingNodeUrl() {
		return new StartingTreeNodeSupplier(requestExecutor)
				.getStart(ldesMetaData.getStartingNodeUrl(), ldesMetaData.getLang())
				.getStartingNodeUrl();
	}

	private List<String> fetchRelations(String nodeUrl) {
		List<String> responseRelations = treeNodeFetcher.fetchTreeRelations(ldesMetaData.createRequest(nodeUrl));
		relations.put(nodeUrl, responseRelations);
		log.debug("{} relation(s) found for nodeUrl {}", responseRelations.size(), nodeUrl);
		return responseRelations;
	}

}
