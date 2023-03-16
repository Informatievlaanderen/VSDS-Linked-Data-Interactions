package be.vlaanderen.informatievlaanderen.ldes.ldi.client;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.StartingTreeNodeSupplier;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.StartingTreeNode;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistanceStrategy;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClientProperties.*;

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
		log.info("Starting LdesClientRunner run setup");
		MemberSupplier memberSupplier = getMemberSupplier();
		log.info("LdesClientRunner setup finished");
		while (threadRunning) {
			componentExecutor.transformLinkedData(memberSupplier.get().getModel());
		}
	}

	private MemberSupplier getMemberSupplier() {
		String targetUrl = properties.getProperty(URL);
		Lang sourceFormat = properties.getOptionalProperty(SOURCE_FORMAT)
				.map(RDFLanguages::nameToLang)
				.orElse(Lang.JSONLD);
		StatePersistanceStrategy state = StatePersistanceStrategy
				.valueOf(properties.getOptionalProperty(STATE).orElse(StatePersistanceStrategy.MEMORY.name()));
		StartingTreeNode startingTreeNode = new StartingTreeNodeSupplier(requestExecutor).getStart(targetUrl,
				sourceFormat);
		TreeNodeProcessor treeNodeProcessor = getTreeNodeProcessor(state, requestExecutor, startingTreeNode);
		boolean keepState = properties.getOptionalProperty(KEEP_STATE)
				.map(Boolean::valueOf)
				.orElse(false);
		return new MemberSupplier(treeNodeProcessor, keepState);
	}

	private TreeNodeProcessor getTreeNodeProcessor(StatePersistanceStrategy statePersistanceStrategy,
			RequestExecutor requestExecutor,
			StartingTreeNode startingTreeNode) {

		return new TreeNodeProcessor(startingTreeNode, statePersistanceStrategy,
				new TreeNodeFetcher(requestExecutor));
	}

	public void stopThread() {
		threadRunning = false;
	}

}
