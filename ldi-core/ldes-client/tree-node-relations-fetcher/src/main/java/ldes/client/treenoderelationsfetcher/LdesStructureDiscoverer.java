package ldes.client.treenoderelationsfetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.LdesStructure;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeNodeRequest;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeRelation;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LdesStructureDiscoverer {
	private static final Logger log = LoggerFactory.getLogger(LdesStructureDiscoverer.class);
	private final TreeRelationsFetcher treeNodeFetcher;
	private final Lang sourceFormat;
	private final RequestExecutor requestExecutor;
	private final LdesStructure ldesStructure;

	public LdesStructureDiscoverer(String startingUrl, Lang sourceFormat, RequestExecutor requestExecutor) {
		this.sourceFormat = sourceFormat;
		this.requestExecutor = requestExecutor;
		this.treeNodeFetcher = new TreeRelationsFetcher(requestExecutor);
		this.ldesStructure = new LdesStructure(startingUrl);
	}

	public LdesStructure discoverLdesStructure() {
		getStartingTreeRelations()
				.parallelStream()
				.forEach(relation -> {
					ldesStructure.addRelation(relation);
					fetchRelations(relation);
				});
		return ldesStructure;
	}

	private List<TreeRelation> getStartingTreeRelations() {
		return new StartingTreeNodeUrlSupplier(requestExecutor)
				.getStartingTreeNodeRelations(ldesStructure.getUri(), sourceFormat)
				.stream()
				.map(startingNode -> new TreeRelation(startingNode.getUrl(), true))
				.toList();
	}


	private void fetchRelations(TreeRelation relation) {
		final TreeNodeRequest treeNodeRequest = new TreeNodeRequest(relation.getUri(), sourceFormat);
		try {
			treeNodeFetcher.fetchTreeRelations(treeNodeRequest)
					.parallelStream()
					.forEach(fetchedRelation -> {
						if(fetchedRelation.isRequired() || ldesStructure.containsChild(relation)) {
							relation.addRelation(fetchedRelation);
							fetchRelations(fetchedRelation);
						}
					});
			log.debug("{} relation(s) found for nodeUrl {}", relation.countChildRelations(), relation.getUri());
		} catch (UnsupportedOperationException e) {
			log.warn(e.getMessage());
		}
	}
}
