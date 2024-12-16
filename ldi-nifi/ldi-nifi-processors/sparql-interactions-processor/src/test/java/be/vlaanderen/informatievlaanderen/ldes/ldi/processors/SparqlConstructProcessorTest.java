package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.jena.riot.Lang;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties.DATA_SOURCE_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SparqlProcessorProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.assertj.core.api.Assertions.assertThatNoException;
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

		MockFlowFile f = testRunner.getFlowFilesForRelationship(SUCCESS).getFirst();
		assertTrue(f.getContent().contains("Source data!"));
		assertTrue(f.getContent().contains("Inferred data"));
	}

	@Test
	void whenSuccess_andReplace_shouldContainOnlyInferredData() {
		testRunner.setProperty(INCLUDE_ORIGINAL, Boolean.FALSE.toString());
		testRunner.setProperty(SPARQL_CONSTRUCT_QUERY, CONSTRUCT_QUERY);

		testRunner.enqueue(FLOW_FILE_CONTENTS);

		testRunner.run();

		MockFlowFile f = testRunner.getFlowFilesForRelationship(SUCCESS).getFirst();

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

	@Test
	void when_SplitIntoMultipleFlowFiles_then_NoExceptionIsThrown() throws URISyntaxException, IOException {
		final URL query = Objects.requireNonNull(getClass().getClassLoader().getResource("construct_query.rq"));
		final String splitQuery = Files.readString(Path.of(query.toURI()));
		testRunner.setProperty(SPARQL_CONSTRUCT_QUERY, splitQuery);
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.TTL.getHeaderString());

		testRunner.enqueue(getClass().getClassLoader().getResourceAsStream("5-members.ttl"));

		assertThatNoException().isThrownBy(testRunner::run);

		testRunner.assertAllFlowFilesTransferred(SUCCESS, 5);
	}

	@Test
	void when_PreventSplittingByNamedGraph_then_TransferOneFlowFile() throws URISyntaxException, IOException {
		final URL query = Objects.requireNonNull(getClass().getClassLoader().getResource("construct_query.rq"));
		final String splitQuery = Files.readString(Path.of(query.toURI()));
		testRunner.setProperty(SPARQL_CONSTRUCT_QUERY, splitQuery);
		testRunner.setProperty(SPLIT_BY_NAMED_GRAPH, Boolean.FALSE.toString());
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.TTL.getHeaderString());

		testRunner.enqueue(getClass().getClassLoader().getResourceAsStream("5-members.ttl"));

		assertThatNoException().isThrownBy(testRunner::run);

		testRunner.assertAllFlowFilesTransferred(SUCCESS, 1);
	}
}
