package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ModelSplitProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.apache.jena.riot.RDFLanguages.contentTypeToLang;
import static org.apache.nifi.flowfile.attributes.CoreAttributes.MIME_TYPE;
import static org.junit.jupiter.api.Assertions.*;

class ModelSplitProcessorTest {

	private TestRunner testRunner;

	@BeforeEach
	void init() {
		testRunner = TestRunners.newTestRunner(ModelSplitProcessor.class);
	}

	@Test
	@Disabled
	void testProcessor_ConfiguredDataSourceFormat_ShouldOverwriteMime() {
		Model inputModel = RDFParser.source("input.ttl").lang(Lang.TURTLE).build().toModel();
		String inputModelString = RDFWriter.source(inputModel).lang(Lang.JSONLD).build().asString();
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.JSONLD.getHeaderString());
		testRunner.enqueue(inputModelString);
		testRunner.setProperty(SUBJECT_TYPE, "http://schema.org/Movie");

		testRunner.run();

		assertTest(inputModel);
		String mime = testRunner.getFlowFilesForRelationship(SUCCESS).get(0).getAttribute(MIME_TYPE.key());
		assertEquals("application/ld+json", mime);
	}

	@Test
	@Disabled
	void testProcessor_ShouldUseMime_WhenNoDataSourceConfigured() {
		Model inputModel = RDFParser.source("input.ttl").lang(Lang.TURTLE).build().toModel();
		String inputModelString = RDFWriter.source(inputModel).lang(Lang.TURTLE).build().asString();
		MockFlowFile flowFile = testRunner.enqueue(inputModelString);
		flowFile.putAttributes(Map.of(MIME_TYPE.key(), Lang.TURTLE.getHeaderString()));
		testRunner.setProperty(SUBJECT_TYPE, "http://schema.org/Movie");

		testRunner.run();

		assertTest(inputModel);
		String mime = testRunner.getFlowFilesForRelationship(SUCCESS).get(0).getAttribute(MIME_TYPE.key());
		assertEquals("text/turtle", mime);
	}

	private void assertTest(Model inputModel) {
		List<MockFlowFile> splitFiles = testRunner.getFlowFilesForRelationship(SUCCESS);
		assertEquals(2, splitFiles.size());
		List<Model> splitModels = splitFiles
				.stream()
				.map(flowFile -> Pair.of(flowFile.getAttribute(MIME_TYPE.key()), flowFile.getContent()))
				.map(pair -> RDFParser.fromString(pair.getRight()).lang(contentTypeToLang(pair.getLeft())).toModel())
				.toList();
		assertModels(List.of("member1.ttl", "member2.ttl"), splitModels);

		List<MockFlowFile> processedFiles = testRunner.getFlowFilesForRelationship(PROCESSED_INPUT_FILE);
		assertEquals(1, processedFiles.size());
		MockFlowFile processedFile = processedFiles.get(0);
		Model resultModel = RDFParser
				.fromString(processedFile.getContent())
				.lang(contentTypeToLang(processedFile.getAttribute(CoreAttributes.MIME_TYPE.key())))
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