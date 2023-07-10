package be.vlaanderen.informatievlaanderen.ldes.ldio;

import java.io.File;
import java.time.LocalDateTime;

public class ArchiveDirectory {

	private final String archiveRootDir;
	private final LocalDateTime memberTimestamp;

	public ArchiveDirectory(String archiveRootDir, LocalDateTime memberTimestamp) {
		this.archiveRootDir = archiveRootDir;
		this.memberTimestamp = memberTimestamp;
	}

	/**
	 * Returns the directory where the file should be archived.
	 * This is based on the timestamp and archiveRootDir.
	 * Example:
	 * - With archiveRootDir = /archive
	 * - With timestamp = 15 feb 2023 11:00:00
	 * <p>
	 * Will Return: /archive/2023/02/15
	 */
	public String getDirectory() {
		return archiveRootDir +
				File.separator +
				memberTimestamp.getYear() +
				File.separator +
				memberTimestamp.getMonthValue() +
				File.separator +
				memberTimestamp.getDayOfMonth() +
				File.separator;
	}

}
