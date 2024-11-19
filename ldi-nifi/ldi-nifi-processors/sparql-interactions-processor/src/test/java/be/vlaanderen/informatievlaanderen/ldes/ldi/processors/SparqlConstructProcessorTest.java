package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SparqlProcessorProperties.INCLUDE_ORIGINAL;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SparqlProcessorProperties.SPARQL_CONSTRUCT_QUERY;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SparqlConstructProcessorTest {

	private TestRunner testRunner;

	@BeforeEach
	void init() {
		testRunner = TestRunners.newTestRunner(SparqlConstructProcessor.class);
	}

	private static final String CONSTRUCT_QUERY = """
			CONSTRUCT {
			  <http://inferred-triple/> <http://test/> "Inferred data"
			}
			WHERE { ?s ?p ?o }
			""";

	private static final String FLOW_FILE_CONTENTS = """
			<http://data-in-flowfile/> <http://test/> "Source data!" .
			""";

	@Test
	void whenSuccess_andInfer_shouldContainSourceAndInferredData() {
		testRunner.setProperty(INCLUDE_ORIGINAL, Boolean.TRUE.toString());
		testRunner.setProperty(SPARQL_CONSTRUCT_QUERY, CONSTRUCT_QUERY);

		testRunner.enqueue(FLOW_FILE_CONTENTS);

		testRunner.run();

		MockFlowFile f = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
		assertTrue(f.getContent().contains("Source data!"));
		assertTrue(f.getContent().contains("Inferred data"));
	}

	@Test
	void whenSuccess_andReplace_shouldContainOnlyInferredData() {
		testRunner.setProperty(INCLUDE_ORIGINAL, Boolean.FALSE.toString());
		testRunner.setProperty(SPARQL_CONSTRUCT_QUERY, CONSTRUCT_QUERY);

		testRunner.enqueue(FLOW_FILE_CONTENTS);

		testRunner.run();

		MockFlowFile f = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);

		// In replace mode, source data shouldn't be present.
		assertFalse(f.getContent().contains("Source data!"));
		assertTrue(f.getContent().contains("Inferred data"));
	}

	@Test
	void whenQueryInvalid_shouldReturnInFailureRouting() {
		final String invalidQuery = "SELECT ?s ?p ?o { ?s ?p ?o }";
		testRunner.setProperty(SPARQL_CONSTRUCT_QUERY, invalidQuery);

		testRunner.enqueue(FLOW_FILE_CONTENTS);

		testRunner.run();

		testRunner.assertAllFlowFilesTransferred(FAILURE);
	}

}
