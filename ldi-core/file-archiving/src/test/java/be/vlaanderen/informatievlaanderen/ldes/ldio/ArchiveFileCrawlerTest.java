package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.separatorsToSystem;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArchiveFileCrawlerTest {

	@Test
	void streamArchiveFilePaths_should_CrawlTheArchiveInLexicalOrder() {
		Path archivePath = Paths.get(separatorsToSystem("src/test/resources/archive"));

		List<Path> result = new ArchiveFileCrawler(archivePath).streamArchiveFilePaths().toList();

		assertEquals(10, result.size());
		assertEquals("src/test/resources/archive/2021/05/01.nq", result.get(0).toString());
		assertEquals("src/test/resources/archive/2021/05/02.nq", result.get(1).toString());
		assertEquals("src/test/resources/archive/2021/05/11.nq", result.get(2).toString());
		assertEquals("src/test/resources/archive/2021/11/02.nq", result.get(3).toString());
		assertEquals("src/test/resources/archive/2021/11/11.nq", result.get(4).toString());
		assertEquals("src/test/resources/archive/2022/05/01.nq", result.get(5).toString());
		assertEquals("src/test/resources/archive/2022/05/02.nq", result.get(6).toString());
		assertEquals("src/test/resources/archive/2022/05/11.nq", result.get(7).toString());
		assertEquals("src/test/resources/archive/2022/11/02.nq", result.get(8).toString());
		assertEquals("src/test/resources/archive/2022/11/11.nq", result.get(9).toString());
	}

}