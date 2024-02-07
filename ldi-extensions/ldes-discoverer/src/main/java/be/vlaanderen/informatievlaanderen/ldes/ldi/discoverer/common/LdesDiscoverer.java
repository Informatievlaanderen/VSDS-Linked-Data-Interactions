package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.common;

import be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.config.LdesDiscovererConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import ldes.client.treenoderelationsfetcher.TreeNodeDiscoverer;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeNodeRelation;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LdesDiscoverer implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(LdesDiscoverer.class);
	private final LdesDiscovererConfig config;
	private final RequestExecutor requestExecutor;

	public LdesDiscoverer(LdesDiscovererConfig config) {
		this.config = config;
		this.requestExecutor = new RequestExecutorFactory().createNoAuthExecutor();
	}

	@Override
	public void run(String... args) {
		log.info("Running LDESDiscoverer for url {}", config.getUrl());

		final List<TreeNodeRelation> relations = discoverRelations();
		final Model ldesStructure = buildLdesStructure(relations);
		final String ldesStructureString = RDFWriter
				.source(ldesStructure)
				.lang(config.getOutputFormatAsLang())
				.asString();

		log.info("Total of {} relations found for url {}\n\n{}", relations.size(), config.getUrl(), ldesStructureString);
	}

	protected List<TreeNodeRelation> discoverRelations() {
		final TreeNodeDiscoverer treeNodeDiscoverer = new TreeNodeDiscoverer(config.getUrl(), config.getSourceFormatAsLang(), requestExecutor);
		return treeNodeDiscoverer.discoverNodes();
	}

	protected Model buildLdesStructure(List<TreeNodeRelation> relations) {
		final Model ldesStructure = ModelFactory.createDefaultModel();
		relations.stream()
				.map(TreeNodeRelation::getRelationModel)
				.forEach(ldesStructure::add);
		return PrefixAdder.addPrefixesToModel(ldesStructure);
	}
}