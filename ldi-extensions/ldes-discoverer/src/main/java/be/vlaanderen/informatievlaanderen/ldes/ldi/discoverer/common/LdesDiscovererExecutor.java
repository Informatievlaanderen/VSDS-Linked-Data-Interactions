package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.common;

import be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.config.LdesDiscovererConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import ldes.client.treenoderelationsfetcher.LdesStructureDiscoverer;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.LdesStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LdesDiscovererExecutor implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(LdesDiscovererExecutor.class);
	private final LdesDiscovererConfig config;
	private final LdesStructureDiscoverer ldesStructureDiscoverer;

	public LdesDiscovererExecutor(LdesDiscovererConfig config) {
		this.config = config;
		final RequestExecutor requestExecutor = new RequestExecutorFactory(false).createNoAuthExecutor();
		ldesStructureDiscoverer = new LdesStructureDiscoverer(config.getUrl(), config.getSourceFormatAsLang(), requestExecutor);
	}

	@Override
	public void run(String... args) {
		log.info("Running LDESDiscoverer for url {}", config.getUrl());

		try {
			final LdesStructure ldesStructure = ldesStructureDiscoverer.discoverLdesStructure();

			log.atInfo().log(ldesStructure.toString());
		} catch (Exception e) {
			log.atError().log(e.getMessage());
		}
	}

}