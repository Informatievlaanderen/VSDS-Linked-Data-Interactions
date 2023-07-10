package be.vlaanderen.informatievlaanderen.ldes.ldio;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// TODO: 07/07/23 test
public class FileName {

	private static final DateTimeFormatter fileNameFormatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd-HH-mm-ss-SSSSSSSSS");

	private final LocalDateTime timestamp;
	private final ArchiveDirectory archiveDirectory;

	public FileName(LocalDateTime timestamp, ArchiveDirectory archiveDirectory) {
		this.timestamp = timestamp;
		this.archiveDirectory = archiveDirectory;
	}

	/**
	 * Filenames need to be unique.
	 * If there are multiple members with the same timestamp we add an index after
	 * the name.
	 *
	 * @return the full filePath
	 *         Example unique filename:
	 *         base-path/2023-12-12-05-05-00-000000000.nq
	 *         <p>
	 *         Example when three members have this timestamp:
	 *         base-path/2023-12-12-05-05-00-000000000.nq
	 *         base-path/2023-12-12-05-05-00-000000000-1.nq
	 *         base-path/2023-12-12-05-05-00-000000000-2.nq
	 *
	 */
	public String getFilePath() {
		String basePath = archiveDirectory.getDirectory();
		String fileName = basePath + "/" + timestamp.format(fileNameFormatter);

		String filePathString = fileName + ".nq";
		if (!Files.isReadable(Paths.get(filePathString))) {
			return filePathString;
		}

		int count = 1;
		while (Files.isReadable(Paths.get(filePathFrom(fileName, count)))) {
			count++;
		}
		return filePathFrom(fileName, count);
	}

	private String filePathFrom(String fileName, int count) {
		return fileName + "-" + count + ".nq";
	}
}
