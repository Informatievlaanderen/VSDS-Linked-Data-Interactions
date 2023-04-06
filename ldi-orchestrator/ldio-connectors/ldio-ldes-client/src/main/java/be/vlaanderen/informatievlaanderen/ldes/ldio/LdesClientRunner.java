package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.StartingTreeNodeSupplier;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.StartingTreeNode;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdesClientRunner implements Runnable {

	private final Logger log = LoggerFactory.getLogger(LdesClientRunner.class);

	private final ComponentExecutor componentExecutor;
	private final RequestExecutor requestExecutor;
	private final ComponentProperties properties;

	private boolean threadRunning = true;

	public LdesClientRunner(RequestExecutor requestExecutor, ComponentProperties properties,
			ComponentExecutor componentExecutor) {
		this.componentExecutor = componentExecutor;
		this.requestExecutor = requestExecutor;
		this.properties = properties;
	}

	@Override
	public void run() {
		try {
			log.info("Starting LdesClientRunner run setup");
			MemberSupplier memberSupplier = getMemberSupplier();
			log.info("LdesClientRunner setup finished");
			while (threadRunning) {
				componentExecutor.transformLinkedData(memberSupplier.get().getModel());
			}
		} catch (Exception e) {
			log.error("LdesClientRunner FAILURE", e);
		}
	}

	private MemberSupplier getMemberSupplier() {
		String targetUrl = properties.getProperty(LdioLdesClientProperties.URL);
		Lang sourceFormat = properties.getOptionalProperty(LdioLdesClientProperties.SOURCE_FORMAT)
				.map(RDFLanguages::nameToLang)
				.orElse(Lang.JSONLD);
		StatePersistenceStrategy state = properties.getOptionalProperty(LdioLdesClientProperties.STATE)
				.flatMap(StatePersistenceStrategy::from)
				.orElse(StatePersistenceStrategy.MEMORY);
		StartingTreeNode startingTreeNode = new StartingTreeNodeSupplier(requestExecutor).getStart(targetUrl,
				sourceFormat);
		TreeNodeProcessor treeNodeProcessor = getTreeNodeProcessor(state, requestExecutor, startingTreeNode);
		boolean keepState = properties.getOptionalBoolean(LdioLdesClientProperties.KEEP_STATE).orElse(false);
		return new MemberSupplier(treeNodeProcessor, keepState);
	}

	private TreeNodeProcessor getTreeNodeProcessor(StatePersistenceStrategy statePersistenceStrategy,
			RequestExecutor requestExecutor,
			StartingTreeNode startingTreeNode) {

		return new TreeNodeProcessor(startingTreeNode, statePersistenceStrategy,
				new TreeNodeFetcher(requestExecutor));
	}

	public void stopThread() {
		threadRunning = false;
	}

}
