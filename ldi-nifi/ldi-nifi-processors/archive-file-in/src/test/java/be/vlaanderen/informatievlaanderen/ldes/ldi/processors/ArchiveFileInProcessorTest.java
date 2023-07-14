package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ArchiveFileInProperties.ARCHIVE_ROOT_DIR;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.apache.commons.io.FilenameUtils.separatorsToSystem;
import static org.junit.jupiter.api.Assertions.*;

class ArchiveFileInProcessorTest {

	private TestRunner testRunner;

	private final String archiveDir = separatorsToSystem("src/test/resources/archive");

	@BeforeEach
	void init() {
		testRunner = TestRunners.newTestRunner(ArchiveFileInProcessor.class);
	}

	@Test
	void testProcessor() {
		testRunner.setProperty(ARCHIVE_ROOT_DIR, archiveDir);

		testRunner.run();

		List<MockFlowFile> successFiles = testRunner.getFlowFilesForRelationship(SUCCESS);

		assertTrue(testRunner.getFlowFilesForRelationship(FAILURE).isEmpty());
		assertEquals(2, successFiles.size());
		assertModel(successFiles.get(0).getContent(), "2022/04/19/2022-04-19-12-12-49-470000000.nq");
		assertModel(successFiles.get(1).getContent(), "2022/05/06/2022-05-06-06-56-41-407000000.nq");
	}

	private void assertModel(String resultModelString, String relativeFilePath) {
		Path path = Paths.get(separatorsToSystem(archiveDir + "/" + relativeFilePath));
		Model expectedModel = RDFParser.source(path).toModel();
		Model actualModel = RDFParser.fromString(resultModelString).lang(Lang.NQUADS).toModel();
		assertTrue(expectedModel.isIsomorphicWith(actualModel));
	}

	@Test
	void testFailure() {
		testRunner.setProperty(ARCHIVE_ROOT_DIR, separatorsToSystem("src/test/resources/invalid-archive"));

		testRunner.run();

		assertTrue(testRunner.getFlowFilesForRelationship(SUCCESS).isEmpty());
		assertFalse(testRunner.getFlowFilesForRelationship(FAILURE).isEmpty());
	}

}