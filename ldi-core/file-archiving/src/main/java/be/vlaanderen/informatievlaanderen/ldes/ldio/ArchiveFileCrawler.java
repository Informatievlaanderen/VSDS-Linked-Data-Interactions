package be.vlaanderen.informatievlaanderen.ldes.ldio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class ArchiveFileCrawler {

    private final Path archiveRootDir;

    public ArchiveFileCrawler(Path archiveRootDir) {
        this.archiveRootDir = archiveRootDir;
    }

    public Stream<Path> streamArchiveFilePaths() throws IOException {
        return readFilesInOrder(archiveRootDir);
    }

    private Stream<Path> readFilesInOrder(Path parent) throws IOException {
        try (final Stream<Path> files = Files.list(parent)) {
            return files
                    .sorted(sortByLowerCaseFileName())
                    .flatMap(this::getFilesFromChildren);
        }
    }

    private Comparator<Path> sortByLowerCaseFileName() {
        return Comparator.comparing(f -> f.toFile().getName().toLowerCase());
    }

    private Stream<Path> getFilesFromChildren(Path child) {
        try {
            if (!Files.isDirectory(child)) {
                return Stream.of(child);
            }
            return readFilesInOrder(child);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
