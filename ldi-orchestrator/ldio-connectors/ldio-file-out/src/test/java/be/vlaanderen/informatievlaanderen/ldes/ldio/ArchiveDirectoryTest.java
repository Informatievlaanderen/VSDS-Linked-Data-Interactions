package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArchiveDirectoryTest {

	@Test
	void should_ReturnDateBasedDirectory_when_CallingGetDirectory() {
		var archiveDirectory = new ArchiveDirectory("archive", LocalDateTime.of(2023, 7, 6, 15, 21));

		String result = archiveDirectory.getDirectory();

		assertEquals(FilenameUtils.separatorsToSystem("archive/2023/7/6/"), result);
	}
}