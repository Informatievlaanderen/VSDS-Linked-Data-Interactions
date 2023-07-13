package be.vlaanderen.informatievlaanderen.ldes.ldio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

@SuppressWarnings("java:S112")
public class ArchiveFileCrawler {

    private final Path archiveRootDir;

    public ArchiveFileCrawler(Path archiveRootDir) {
        this.archiveRootDir = archiveRootDir;
    }

    public Stream<Path> streamArchiveFilePaths() {
        return filesInDir(archiveRootDir);
    }

    private Stream<Path> filesInDir(Path dirPath) {
        return listFiles(dirPath)
                .sorted(sortByLowerCaseFileName())
                .flatMap(path -> Files.isDirectory(path) ? filesInDir(path) : Stream.of(path));
    }

    private Stream<Path> listFiles(Path path) {
        try {
            return Files.list(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Comparator<Path> sortByLowerCaseFileName() {
        return Comparator.comparing(f -> f.toFile().getName().toLowerCase());
    }

}
