package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

public class LdioFileOut implements LdiOutput {

    private final TimestampExtractor timestampExtractor;
    private final String archiveRootDir;

    public LdioFileOut(TimestampExtractor timestampExtractor, String archiveRootDir) {
        this.timestampExtractor = timestampExtractor;
        this.archiveRootDir = archiveRootDir;
    }

    @Override
    public void accept(Model model) {
        ArchiveFile archiveFile = new ArchiveFile(model, timestampExtractor);
        RDFWriter.source(model).lang(Lang.NQUADS).output(archiveFile.getPath(archiveRootDir));
    }

}
