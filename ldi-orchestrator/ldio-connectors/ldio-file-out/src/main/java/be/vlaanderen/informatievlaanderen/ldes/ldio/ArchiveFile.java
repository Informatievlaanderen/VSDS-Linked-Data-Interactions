package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public class ArchiveFile {

    private final Model model;
    private final TimestampExtractor timestampExtractor;

    public ArchiveFile(Model model, TimestampExtractor timestampExtractor) {
        this.model = model;
        this.timestampExtractor = timestampExtractor;
    }

    /**
     * Returns the path of the archive file based on the timestamp property of the model
     * @param rootDir root directory where the archive is located
     * @return the full path of the archive file
     * <p>
     *     example:
     *          my-archive/2023/11/21/2023-11-21-05-05-00-000000000-2.nq
     * </p>
     */
    // TODO: 07/07/23 test
    public String getPath(String rootDir) {
        LocalDateTime timestamp = timestampExtractor.extractTimestamp(model);
        ArchiveDirectory archiveDirectory = new ArchiveDirectory(rootDir, timestamp);
        return new FileName(timestamp, archiveDirectory).getFilePath();
    }

}
