package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

import java.io.IOException;
import java.nio.file.Files;

public class LdioFileOut implements LdiOutput {

    private final TimestampExtractor timestampExtractor;
    private final String archiveRootDir;

    public LdioFileOut(TimestampExtractor timestampExtractor, String archiveRootDir) {
        this.timestampExtractor = timestampExtractor;
        this.archiveRootDir = archiveRootDir;
    }

    @Override
    public void accept(Model model) {
        ArchiveFile archiveFile = ArchiveFile.from(model, timestampExtractor, archiveRootDir);
        try {
            Files.createDirectories(archiveFile.getDirectoryPath());
            RDFWriter.source(model).lang(Lang.NQUADS).output(archiveFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write model to file in archive directory", e);
        }
    }

}
