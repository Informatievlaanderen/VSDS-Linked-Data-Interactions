package ldes.client.treenoderelationsfetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.LdesStructure;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeNodeRequest;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeRelation;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Responsible for discovering the entire structure below a provided starting uri
 */
public class LdesStructureDiscoverer {
	private static final Logger log = LoggerFactory.getLogger(LdesStructureDiscoverer.class);
	private final String startingUrl;
	private final Lang sourceFormat;
	private final RequestExecutor requestExecutor;

	public LdesStructureDiscoverer(String startingUrl, Lang sourceFormat, RequestExecutor requestExecutor) {
		this.startingUrl = startingUrl;
		this.sourceFormat = sourceFormat;
		this.requestExecutor = requestExecutor;
	}

	/**
	 * @return the entire structure of the (sub)set below the starting uri
	 */
	public LdesStructure discoverLdesStructure() {
		final LdesStructure ldesStructure = new LdesStructure(startingUrl);
		getStartingTreeRelations()
				.parallelStream()
				.forEach(relation -> {
					ldesStructure.addRelation(relation);
					fetchRelations(relation, true);
				});
		return ldesStructure;
	}

	private List<TreeRelation> getStartingTreeRelations() {
		return new StartingTreeNodeUrlSupplier(requestExecutor)
				.getStartingTreeNodeRelations(startingUrl, sourceFormat)
				.stream()
				.map(startingNode -> new TreeRelation(startingNode.getUrl(), true))
				.toList();
	}


	private void fetchRelations(TreeRelation relation, boolean isChildOfRoot) {
		final TreeNodeRequest treeNodeRequest = new TreeNodeRequest(relation.getUri(), sourceFormat);
		try {
			new TreeRelationsFetcher(requestExecutor).fetchTreeRelations(treeNodeRequest)
					.parallelStream()
					.forEach(fetchedRelation -> {
						if (fetchedRelation.isRequired() || isChildOfRoot) {
							relation.addRelation(fetchedRelation);
							fetchRelations(fetchedRelation, false);
						}
					});
			log.debug("{} relation(s) found for nodeUrl {}", relation.countChildRelations(), relation.getUri());
		} catch (UnsupportedOperationException e) {
			log.warn(e.getMessage());
		}
	}
}
