package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.EndOfLdesException;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdesClientRunner implements Runnable {

	private final Logger log = LoggerFactory.getLogger(LdesClientRunner.class);

	private final ComponentExecutor componentExecutor;
	private final RequestExecutor requestExecutor;
	private final ComponentProperties properties;
	private final StatePersistence statePersistence;

	private boolean threadRunning = true;

	public LdesClientRunner(RequestExecutor requestExecutor, ComponentProperties properties,
			ComponentExecutor componentExecutor, StatePersistence statePersistence) {
		this.componentExecutor = componentExecutor;
		this.requestExecutor = requestExecutor;
		this.properties = properties;
		this.statePersistence = statePersistence;
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
		} catch (EndOfLdesException e) {
			log.warn(e.getMessage());
		} catch (Exception e) {
			log.error("LdesClientRunner FAILURE", e);
		}
	}

	private MemberSupplier getMemberSupplier() {
		String targetUrl = properties.getProperty(LdioLdesClientProperties.URL);
		Lang sourceFormat = properties.getOptionalProperty(LdioLdesClientProperties.SOURCE_FORMAT)
				.map(RDFLanguages::nameToLang)
				.orElse(Lang.JSONLD);
		LdesMetaData ldesMetaData = new LdesMetaData(targetUrl, sourceFormat);
		TreeNodeProcessor treeNodeProcessor = getTreeNodeProcessor(statePersistence, requestExecutor, ldesMetaData);
		boolean keepState = properties.getOptionalBoolean(LdioLdesClientProperties.KEEP_STATE).orElse(false);

		return new MemberSupplier(treeNodeProcessor, keepState);
	}

	private TreeNodeProcessor getTreeNodeProcessor(StatePersistence statePersistenceStrategy,
			RequestExecutor requestExecutor,
			LdesMetaData ldesMetaData) {
		return new TreeNodeProcessor(ldesMetaData, statePersistenceStrategy, requestExecutor);
	}

	public void stopThread() {
		threadRunning = false;
	}

}
