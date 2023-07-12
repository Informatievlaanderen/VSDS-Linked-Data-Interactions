package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.io.IOException;

public class LdioArchiveFileIn extends LdiInput {

    private final ArchiveFileReader archiveFileReader;
    private final Lang sourceFormat;

    public LdioArchiveFileIn(LdiAdapter adapter, ComponentExecutor executor,
                             ArchiveFileReader archiveFileReader, Lang sourceFormat) {
        super(executor, adapter);
        this.archiveFileReader = archiveFileReader;
        this.sourceFormat = sourceFormat;
    }

    public void run() throws IOException {
        archiveFileReader.readFiles().forEach(file -> {
            Model model = RDFParser.source(file).lang(sourceFormat).toModel();
            getExecutor().transformLinkedData(model);
        });
    }

}
