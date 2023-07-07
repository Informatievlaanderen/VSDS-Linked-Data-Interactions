package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

import java.time.LocalDateTime;

public class LdioFileOut implements LdiOutput {

    private final FileNameManager fileNameManager;
    private final DirectoryManager directoryManager;
    private final TimestampExtractor timestampExtractor;

    public LdioFileOut(FileNameManager fileNameManager, DirectoryManager directoryManager, TimestampExtractor timestampExtractor) {
        this.fileNameManager = fileNameManager;
        this.directoryManager = directoryManager;
        this.timestampExtractor = timestampExtractor;
    }

    @Override
    public void accept(Model model) {
        LocalDateTime timestamp = timestampExtractor.extractTimestamp(model);
        String directoryPath = directoryManager.createDirectoryForArchiving(timestamp);
        String filePath = fileNameManager.createFilePath(timestamp, directoryPath);
        RDFWriter.source(model).lang(Lang.NQUADS).output(filePath);
    }

}
