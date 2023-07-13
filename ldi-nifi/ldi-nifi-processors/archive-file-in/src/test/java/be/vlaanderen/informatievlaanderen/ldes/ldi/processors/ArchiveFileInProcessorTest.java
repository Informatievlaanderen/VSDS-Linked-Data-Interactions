package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ArchiveFileInProperties.ARCHIVE_ROOT_DIR;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.apache.commons.io.FilenameUtils.separatorsToSystem;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchiveFileInProcessorTest {

	private TestRunner testRunner;

	private final String archiveDir = "archive";

	@BeforeEach
	void init() {
		testRunner = TestRunners.newTestRunner(ArchiveFileInProcessor.class);
	}

	@Test
	void testProcessor() {
		// TODO: 13/07/23 implement
		assertTrue(false);
		// Model inputModel =
		// RDFParser.source("model-with-timestamp20220520.nq").toModel();
		// String inputModelString =
		// RDFWriter.source(inputModel).lang(Lang.NQUADS).build().asString();
		//
		// testRunner.setProperty(ARCHIVE_ROOT_DIR, archiveDir);
		// testRunner.run();
		//
		// assertFalse(testRunner.getFlowFilesForRelationship(SUCCESS).isEmpty());
		// assertTrue(testRunner.getFlowFilesForRelationship(FAILURE).isEmpty());
		//
		// Model resultModel = RDFParser.source(archiveDir
		// +
		// separatorsToSystem("/2022/05/20/2022-05-20-09-58-15-867000000.nq")).toModel();
		// assertTrue(inputModel.isIsomorphicWith(resultModel));
	}

	@Test
	void testFailure() {
		testRunner.setProperty(ARCHIVE_ROOT_DIR, separatorsToSystem("src/test/resources/invalid-archive"));
		testRunner.enqueue("random");
		testRunner.run();

		assertTrue(testRunner.getFlowFilesForRelationship(SUCCESS).isEmpty());
		assertFalse(testRunner.getFlowFilesForRelationship(FAILURE).isEmpty());
	}

}