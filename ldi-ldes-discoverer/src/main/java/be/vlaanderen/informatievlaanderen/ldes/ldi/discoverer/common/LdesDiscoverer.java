package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.common;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRelation;
import ldes.client.treenodesupplier.TreeNodeDiscoverer;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LdesDiscoverer implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(LdesDiscoverer.class);
	private final LdesMetaData ldesMetaData;
	private final Lang outputFormat;
	private final RequestExecutor requestExecutor;

	public LdesDiscoverer(@Value("${discoverer.url:}") String endpoint,
						  @Value("${discoverer.source-format:application/n-quads}") String sourceFormat,
						  @Value("${discoverer.output-format:text/turtle}") String outputFormat) {
		Lang sourceLang = RDFLanguages.nameToLang(sourceFormat);
		this.ldesMetaData = new LdesMetaData(endpoint, sourceLang);
		this.outputFormat = RDFLanguages.nameToLang(outputFormat);
		this.requestExecutor = new RequestExecutorFactory().createNoAuthExecutor();
	}

	@Override
	public void run(String... args) {
		log.info("Running LDESDiscoverer for url {}", ldesMetaData.getStartingNodeUrl());
		TreeNodeDiscoverer treeNodeDiscoverer = new TreeNodeDiscoverer(ldesMetaData, requestExecutor);
		List<TreeNodeRelation> relations = treeNodeDiscoverer.discoverNodes();
		Model ldesStructure = ModelFactory.createDefaultModel();
		relations.stream()
				.map(TreeNodeRelation::getRelationModel)
				.forEach(ldesStructure::add);
		String ldesStructureString = RDFWriter.source(PrefixAdder.addPrefixesToModel(ldesStructure)).lang(outputFormat).asString();
		log.info("Total of {} relations found for url {}\n\n{}", relations.size(), ldesMetaData.getStartingNodeUrl(), ldesStructureString);
	}
}