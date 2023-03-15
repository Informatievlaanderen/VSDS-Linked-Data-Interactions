package be.vlaanderen.informatievlaanderen.ldes.ldi.client;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodesupplier.LdesProvider;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.Ldes;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistanceStrategy;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClientProperties.*;

public class LdesClientRunner implements Runnable {
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
		String targetUrl = properties.getProperty(URL);
		Lang sourceFormat = properties.getOptionalProperty(SOURCE_FORMAT)
				.map(RDFLanguages::nameToLang)
				.orElse(Lang.JSONLD);
		StatePersistanceStrategy state = StatePersistanceStrategy
				.valueOf(properties.getOptionalProperty(STATE).orElse(StatePersistanceStrategy.MEMORY.name()));
		boolean keepState = properties.getOptionalBoolean(KEEP_STATE).orElse(false);
		Ldes ldes = new LdesProvider(requestExecutor).getLdes(targetUrl, sourceFormat);
		TreeNodeProcessor treeNodeProcessor = getTreeNodeProcessor(state, requestExecutor, ldes);
		MemberSupplier memberSupplier = new MemberSupplier(treeNodeProcessor, keepState);
		while (threadRunning) {
			componentExecutor.transformLinkedData(memberSupplier.get().getModel());
		}
	}

	private TreeNodeProcessor getTreeNodeProcessor(StatePersistanceStrategy statePersistanceStrategy,
			RequestExecutor requestExecutor,
			Ldes ldes) {

		return new TreeNodeProcessor(ldes, statePersistanceStrategy,
				new TreeNodeFetcher(requestExecutor));
	}

	public void stopThread() {
		threadRunning = false;
	}
}
