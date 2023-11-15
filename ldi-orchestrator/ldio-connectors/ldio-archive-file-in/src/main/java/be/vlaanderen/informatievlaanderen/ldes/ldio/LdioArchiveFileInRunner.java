package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

public class LdioArchiveFileInRunner extends LdioInput implements Runnable {

	private final ArchiveFileCrawler archiveFileCrawler;
	private final Lang sourceFormat;

	public LdioArchiveFileInRunner(String componentName, String pipelineName, ComponentExecutor executor, ArchiveFileCrawler crawler, Lang source) {
		super(componentName, pipelineName, executor, null);
		this.archiveFileCrawler = crawler;
		this.sourceFormat = source;
	}

	public void run() {
		archiveFileCrawler.streamArchiveFilePaths().forEach(file -> {
			Model model = RDFParser.source(file).lang(sourceFormat).toModel();
			processModel(model);
		});
	}

}
