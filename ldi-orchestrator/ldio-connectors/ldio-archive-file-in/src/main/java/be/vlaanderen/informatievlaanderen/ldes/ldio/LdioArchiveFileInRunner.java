package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

public class LdioArchiveFileInRunner implements Runnable {

    private final ArchiveFileCrawler archiveFileCrawler;
    private final Lang sourceFormat;
    private final ComponentExecutor executor;

    public LdioArchiveFileInRunner(ComponentExecutor executor, ArchiveFileCrawler crawler, Lang source) {
        this.archiveFileCrawler = crawler;
        this.sourceFormat = source;
        this.executor = executor;
    }

    public void run() {
        archiveFileCrawler.streamArchiveFilePaths().forEach(file -> {
            Model model = RDFParser.source(file).lang(sourceFormat).toModel();
            executor.transformLinkedData(model);
        });
    }

}
