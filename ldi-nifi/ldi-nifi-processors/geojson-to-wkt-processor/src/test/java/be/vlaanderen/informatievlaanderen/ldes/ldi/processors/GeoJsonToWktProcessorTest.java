package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.GeoJsonToWktProcessorProperties.DATA_SOURCE_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.GeoJsonToWktProcessorProperties.TRANSFORM_TO_RDF_WKT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoJsonToWktProcessorTest {

	private TestRunner testRunner;

	private static Stream<Arguments> testData() {
		return Stream.of(
				Arguments.of("false", "result-all-types.json"),
				Arguments.of("true", "result-all-types-with-blank-nodes.json")
		);
	}

	@BeforeEach
	void init() {
		testRunner = TestRunners.newTestRunner(GeoJsonToWktProcessor.class);
	}

	@ParameterizedTest
	@MethodSource("testData")
	void testProcessor(String transformToRdfWkt, String expectedSourceFile) {
		Model inputModel = RDFParser.source("geojson-all-types.json").lang(Lang.JSONLD).build().toModel();
		String inputModelString = RDFWriter.source(inputModel).lang(Lang.JSONLD).build().asString();

		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.JSONLD.getHeaderString());
		testRunner.setProperty(TRANSFORM_TO_RDF_WKT, transformToRdfWkt);
		testRunner.enqueue(inputModelString);
		testRunner.run();

		MockFlowFile result = testRunner.getFlowFilesForRelationship(SUCCESS).getFirst();
		Model resultModel = RDFParser.fromString(result.getContent()).lang(Lang.JSONLD).build().toModel();
		Model expectedModel = RDFParser.source(expectedSourceFile).lang(Lang.JSONLD).build().toModel();
		assertTrue(expectedModel.isIsomorphicWith(resultModel));
	}

	@Test
	void testFailure() {
		testRunner.enqueue("random");
		testRunner.run();

		assertTrue(testRunner.getFlowFilesForRelationship(SUCCESS).isEmpty());
		assertFalse(testRunner.getFlowFilesForRelationship(FAILURE).isEmpty());
	}
}