package ldes.client.treenoderelationsfetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeNodeRelation;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeNodeRequest;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeNodeDiscoverer {
	private static final Logger log = LoggerFactory.getLogger(TreeNodeDiscoverer.class);
	private final TreeNodeRelationsFetcher treeNodeFetcher;
	private final String startingUrl;
	private final Lang sourceFormat;
	private final RequestExecutor requestExecutor;
	private final List<TreeNodeRelation> relations = new ArrayList<>();

	public TreeNodeDiscoverer(String startingUrl, Lang sourceFormat, RequestExecutor requestExecutor) {
		this.startingUrl = startingUrl;
		this.sourceFormat = sourceFormat;
		this.requestExecutor = requestExecutor;
		this.treeNodeFetcher = new TreeNodeRelationsFetcher(requestExecutor);
	}

	public List<TreeNodeRelation> discoverNodes() {
		String startingNodeUrl = getStartingNodeUrl();
		List<String> fetchedRelations = fetchRelations(startingNodeUrl);
		while (!fetchedRelations.isEmpty()) {
			fetchedRelations = fetchedRelations.parallelStream()
					.flatMap(relation -> fetchRelations(relation).stream())
					.toList();
		}
		return relations;
	}

	private String getStartingNodeUrl() {
		return new StartingTreeNodeUrlSupplier(requestExecutor)
				.getStart(startingUrl, sourceFormat);
	}

	private List<String> fetchRelations(String nodeUrl) {
		final TreeNodeRequest treeNodeRequest = new TreeNodeRequest(nodeUrl, sourceFormat);
		try {
			final List<TreeNodeRelation> responseRelations = treeNodeFetcher.fetchTreeRelations(treeNodeRequest);
			relations.addAll(responseRelations.stream().filter(TreeNodeRelation::isNotEmpty).toList());
			log.debug("{} relation(s) found for nodeUrl {}", responseRelations.size(), nodeUrl);
			return responseRelations.stream().map(TreeNodeRelation::getRelationUri).toList();
		} catch (UnsupportedOperationException e) {
			log.warn(e.getMessage());
		}
		return Collections.emptyList();
	}

}
