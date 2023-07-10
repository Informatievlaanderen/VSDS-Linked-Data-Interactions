package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ArchiveDirectoryTest {

	@Test
	void should_ReturnDateBasedDirectory_when_CallingGetDirectory() {
		var archiveDirectory = new ArchiveDirectory("archive", LocalDateTime.of(2023, 7, 6, 15, 21));

		String result = archiveDirectory.getDirectory();

		assertEquals("archive/2023/7/6/", result);
	}
}