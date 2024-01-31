package be.vlaanderen.informatievlaanderen.ldes.discoverer;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import ldes.client.treenodesupplier.TreeNodeDiscoverer;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LdesDiscoverer implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(LdesDiscoverer.class);
	private final LdesMetaData ldesMetaData;
	private final RequestExecutor requestExecutor;

	public LdesDiscoverer(@Value("${discoverer.url:}") String endpoint, @Value("${discoverer.lang:text/turtle}") String sourceFormat) {
		Lang sourceLang = RDFLanguages.nameToLang(sourceFormat);
		ldesMetaData = new LdesMetaData(endpoint, sourceLang);
		requestExecutor = new RequestExecutorFactory().createNoAuthExecutor();
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Running LDESDiscoverer for url {}", ldesMetaData.getStartingNodeUrl());
		TreeNodeDiscoverer treeNodeDiscoverer = new TreeNodeDiscoverer(ldesMetaData, requestExecutor);
		Map<String, List<String>> relations = treeNodeDiscoverer.discoverNodes();
		log.info("Total of {} relations found for url {}", relations.size(), ldesMetaData.getStartingNodeUrl());
	}
}
