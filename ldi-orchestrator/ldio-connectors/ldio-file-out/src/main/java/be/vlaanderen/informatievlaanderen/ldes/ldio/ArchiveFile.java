package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.jena.rdf.model.Model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class ArchiveFile {

    private final FileName fileName;
    private final ArchiveDirectory archiveDirectory;

    private ArchiveFile(FileName fileName, ArchiveDirectory archiveDirectory) {
        this.fileName = fileName;
        this.archiveDirectory = archiveDirectory;
    }

    // TODO: 10/07/23 test
    /**
     * Creates a new ArchiveFile
     * @param model to be archived
     * @param timestampExtractor extractor to obtain timestamp from model
     * @param archiveRootDir root directory of archive
     * @return new instance of ArchiveFile
     */
    public static ArchiveFile from(Model model, TimestampExtractor timestampExtractor, String archiveRootDir) {
        LocalDateTime timestamp = timestampExtractor.extractTimestamp(model);
        ArchiveDirectory directory = new ArchiveDirectory(archiveRootDir, timestamp);
        return new ArchiveFile(new FileName(timestamp, directory), directory);
    }

    /**
     * Returns the path of the archive file based on the timestamp property of the model
     * @return the full path of the archive file
     * <p>
     *     example:
     *          my-archive/2023/11/21/2023-11-21-05-05-00-000000000-2.nq
     * </p>
     */
    // TODO: 07/07/23 test
    public String getPath() {
        return fileName.getFilePath();
    }

    // TODO: 10/07/23 test
    public Path getDirectoryPath() {
        return Paths.get(archiveDirectory.getDirectory());
    }

}
