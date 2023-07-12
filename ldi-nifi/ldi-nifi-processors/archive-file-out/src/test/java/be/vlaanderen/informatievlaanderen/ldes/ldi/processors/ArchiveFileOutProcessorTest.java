package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ArchiveFileOutProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchiveFileOutProcessorTest {

	private TestRunner testRunner;

	final String archiveDir = FilenameUtils.separatorsToSystem("target/archive");

	@BeforeEach
	void init() throws IOException {
		testRunner = TestRunners.newTestRunner(ArchiveFileOutProcessor.class);
		FileUtils.deleteDirectory(new File(archiveDir));
	}

	@Test
	void testProcessor() {
		Model inputModel = RDFParser.source("model-with-timestamp20220520.nq").toModel();
		String inputModelString = RDFWriter.source(inputModel).lang(Lang.NQUADS).build().asString();

		testRunner.setProperty(ARCHIVE_ROOT_DIR, archiveDir);
		testRunner.setProperty(TIMESTAMP_PATH, "http://www.w3.org/ns/prov#generatedAtTime");
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.NQUADS.getHeaderString());
		testRunner.enqueue(inputModelString);
		testRunner.run();

		assertFalse(testRunner.getFlowFilesForRelationship(SUCCESS).isEmpty());
		assertTrue(testRunner.getFlowFilesForRelationship(FAILURE).isEmpty());

		Model resultModel = RDFParser.source(archiveDir
				+ FilenameUtils.separatorsToSystem("/2022/05/20/2022-05-20-09-58-15-867000000.nq")).toModel();
		assertTrue(inputModel.isIsomorphicWith(resultModel));
	}

	@Test
	void testFailure() {
		testRunner.setProperty(ARCHIVE_ROOT_DIR, archiveDir);
		testRunner.setProperty(TIMESTAMP_PATH, "http://www.w3.org/ns/prov#generatedAtTime");
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.NQUADS.getHeaderString());

		testRunner.enqueue("random");
		testRunner.run();

		assertTrue(testRunner.getFlowFilesForRelationship(SUCCESS).isEmpty());
		assertFalse(testRunner.getFlowFilesForRelationship(FAILURE).isEmpty());
	}

}