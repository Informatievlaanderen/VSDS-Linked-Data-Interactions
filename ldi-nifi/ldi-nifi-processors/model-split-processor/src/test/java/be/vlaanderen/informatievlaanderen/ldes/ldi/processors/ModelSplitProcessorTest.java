package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ModelSplitProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

class ModelSplitProcessorTest {

	private TestRunner testRunner;

	@BeforeEach
	void init() {
		testRunner = TestRunners.newTestRunner(ModelSplitProcessor.class);
	}

	@Test
	void testProcessor() {
		Model inputModel = RDFParser.source("input.ttl").lang(Lang.TURTLE).build().toModel();
		String inputModelString = RDFWriter.source(inputModel).lang(Lang.TURTLE).build().asString();

		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.TURTLE.getHeaderString());
		testRunner.setProperty(SUBJECT_TYPE, "http://schema.org/Movie");
		testRunner.enqueue(inputModelString);
		testRunner.run();

		List<MockFlowFile> splitFiles = testRunner.getFlowFilesForRelationship(SUCCESS);
		assertEquals(2, splitFiles.size());
		List<Model> splitModels = splitFiles
				.stream()
				.map(MockFlowFile::getContent)
				.map(content -> RDFParser.fromString(content).lang(Lang.TURTLE).toModel())
				.toList();
		assertModels(List.of("member1.ttl", "member2.ttl"), splitModels);

		List<MockFlowFile> processedFiles = testRunner.getFlowFilesForRelationship(PROCESSED_INPUT_FILE);
		assertEquals(1, processedFiles.size());
		Model resultModel = RDFParser.fromString(processedFiles.get(0).getContent()).lang(Lang.TURTLE).build()
				.toModel();
		assertTrue(inputModel.isIsomorphicWith(resultModel));
	}

	private void assertModels(List<String> expectedModelPaths, List<Model> result) {
		Set<Model> expectedModels = expectedModelPaths
				.stream()
				.map(RDFParser::source)
				.map(RDFParserBuilder::toModel)
				.collect(Collectors.toSet());

		result.forEach(
				actualResult -> expectedModels
						.removeIf(expectedResult -> expectedResult.isIsomorphicWith(actualResult)));

		assertTrue(expectedModels.isEmpty());
	}

	@Test
	void testFailure() {
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.TURTLE.getHeaderString());
		testRunner.setProperty(SUBJECT_TYPE, "http://schema.org/Movie");

		testRunner.enqueue("random");
		testRunner.run();

		assertTrue(testRunner.getFlowFilesForRelationship(SUCCESS).isEmpty());
		assertFalse(testRunner.getFlowFilesForRelationship(FAILURE).isEmpty());
	}

}