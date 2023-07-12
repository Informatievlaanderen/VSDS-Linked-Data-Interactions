package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.io.IOException;

public class LdioArchiveFileIn extends LdiInput {

    private final ArchiveFileCrawler archiveFileCrawler;
    private final Lang sourceFormat;

    public LdioArchiveFileIn(LdiAdapter adapter, ComponentExecutor executor,
                             ArchiveFileCrawler archiveFileCrawler, Lang sourceFormat) {
        super(executor, adapter);
        this.archiveFileCrawler = archiveFileCrawler;
        this.sourceFormat = sourceFormat;
    }

    public void run() throws IOException {
        archiveFileCrawler.streamArchiveFilePaths().forEach(file -> {
            Model model = RDFParser.source(file).lang(sourceFormat).toModel();
            getExecutor().transformLinkedData(model);
        });
    }

}
