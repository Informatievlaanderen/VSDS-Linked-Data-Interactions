package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@SuppressWarnings("java:S112")
public class ArchiveFileCrawler {

	private final Path archiveRootDir;
	private final Logger log = LoggerFactory.getLogger(ArchiveFileCrawler.class);

	public ArchiveFileCrawler(Path archiveRootDir) {
		this.archiveRootDir = archiveRootDir;
	}

	public Stream<Path> streamArchiveFilePaths() {
		return filesInDir(archiveRootDir);
	}

	private Stream<Path> filesInDir(Path dirPath) {
		return listFiles(dirPath)
				.sorted()
				.flatMap(path -> Files.isDirectory(path) ? filesInDir(path) : Stream.of(path));
	}

	private Stream<Path> listFiles(Path path) {
		try {
			return Files.list(path);
		} catch (IOException e) {
			log.error("IOException occurred while crawling the archive files: {}", e.getMessage());
			return Stream.empty();
		}
	}

}
