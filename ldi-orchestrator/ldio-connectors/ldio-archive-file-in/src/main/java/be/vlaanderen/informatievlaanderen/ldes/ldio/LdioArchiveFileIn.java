package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdioArchiveFileIn extends LdioInput {

	private final Logger log = LoggerFactory.getLogger(LdioArchiveFileIn.class);
	private final ArchiveFileCrawler archiveFileCrawler;
	private final Lang sourceFormat;

	public LdioArchiveFileIn(String componentName, String pipelineName, ComponentExecutor executor, ArchiveFileCrawler crawler, Lang source) {
		super(componentName, pipelineName, executor, null);
		this.archiveFileCrawler = crawler;
		this.sourceFormat = source;
		log.info("Starting with crawling the archive.");
		crawlArchive();
		log.info("Finished crawling the archive.");
	}

	public void crawlArchive() {
		archiveFileCrawler.streamArchiveFilePaths().forEach(file -> {
			Model model = RDFParser.source(file).lang(sourceFormat).toModel();
			processModel(model);
		});
	}

}
