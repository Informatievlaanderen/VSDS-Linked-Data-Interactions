package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.TreeRelationsFetcher;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRelation;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TreeNodeDiscoverer {
	private static final Logger log = LoggerFactory.getLogger(TreeNodeDiscoverer.class);
	private final TreeRelationsFetcher treeNodeFetcher;
	private final LdesMetaData ldesMetaData;
	private final RequestExecutor requestExecutor;
	private final List<TreeNodeRelation> relations = new ArrayList<>();

	public TreeNodeDiscoverer(LdesMetaData ldesMetaData, RequestExecutor requestExecutor) {
		this.ldesMetaData = ldesMetaData;
		this.requestExecutor = requestExecutor;
		this.treeNodeFetcher = new TreeRelationsFetcher(requestExecutor);

	}

	public List<TreeNodeRelation> discoverNodes() {
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
		List<TreeNodeRelation> responseRelations = treeNodeFetcher.fetchTreeRelations(ldesMetaData.createRequest(nodeUrl));
		relations.addAll(responseRelations);
		log.debug("{} relation(s) found for nodeUrl {}", responseRelations.size(), nodeUrl);
		return responseRelations.stream().map(TreeNodeRelation::getRelation).toList();
	}

}
