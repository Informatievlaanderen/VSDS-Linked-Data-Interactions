package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SparqlAskRelationships;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SparqlProcessorProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import org.apache.jena.riot.Lang;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties.DATA_SOURCE_FORMAT;

class SparqlAskProcessorTest {
	private static final String ASK_QUERY = "ASK WHERE { %s ?p ?o }";
	private TestRunner testRunner;

	@BeforeEach
	void setUp() {
		testRunner = TestRunners.newTestRunner(SparqlAskProcessor.class);
	}

	@Test
	void test_True() {
		testRunner.setProperty(SparqlProcessorProperties.SPARQL_ASK_QUERY, ASK_QUERY.formatted("<http://somewhere/RebeccaSmith/>"));
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.NQ.getHeaderString());

		testRunner.enqueue(readPeopleData());
		testRunner.run();

		testRunner.assertAllFlowFilesTransferred(SparqlAskRelationships.TRUE);
	}

	@Test
	void test_False() {
		testRunner.setProperty(SparqlProcessorProperties.SPARQL_ASK_QUERY, ASK_QUERY.formatted("<http://somewhere/Fantasy/>"));
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.NQ.getHeaderString());

		testRunner.enqueue(readPeopleData());
		testRunner.run();

		testRunner.assertAllFlowFilesTransferred(SparqlAskRelationships.FALSE);
	}

	@Test
	void test_InvalidQuery() {
		testRunner.setProperty(SparqlProcessorProperties.SPARQL_ASK_QUERY, "ASK ?s ?p ?o WHERE { ?s ?p ?o }");
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.NQ.getHeaderString());

		testRunner.assertNotValid();
	}

	@Test
	void given_invalidModel_test_Failure() {
		testRunner.setProperty(SparqlProcessorProperties.SPARQL_ASK_QUERY, ASK_QUERY.formatted("<http://somewhere/RebeccaSmith/>"));
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.JSONLD.getHeaderString());

		testRunner.enqueue(readPeopleData());
		testRunner.run();

		testRunner.assertAllFlowFilesTransferred(FlowManager.FAILURE);
	}

	@Test
	void given_invalidQuery_test_Failure() {
		testRunner.setProperty(SparqlProcessorProperties.SPARQL_ASK_QUERY, "SELECT * WHERE { ?s ?p ?o }");
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.NQ.getHeaderString());

		testRunner.enqueue(readPeopleData());
		testRunner.run();

		testRunner.assertAllFlowFilesTransferred(FlowManager.FAILURE);
	}

	@Test
	void test_Empty() {
		testRunner.setProperty(SparqlProcessorProperties.SPARQL_ASK_QUERY, ASK_QUERY.formatted("<http://somewhere/RebeccaSmith/>"));
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.NQ.getHeaderString());

		testRunner.run();

		testRunner.assertTransferCount(SparqlAskRelationships.TRUE, 0);
		testRunner.assertTransferCount(SparqlAskRelationships.FALSE, 0);
		testRunner.assertTransferCount(FlowManager.FAILURE, 0);
	}

	@Test
	void test_WithGraph() {
		final String query = """
				ASK WHERE {
				  GRAPH <http://example.org/named-graph> {
				    ?s ?p ?o
				  }
				}
				""";
		testRunner.setProperty(SparqlProcessorProperties.SPARQL_ASK_QUERY, query);
		testRunner.setProperty(DATA_SOURCE_FORMAT, Lang.NQ.getHeaderString());

		testRunner.enqueue(readPeopleData());
		testRunner.run();

		testRunner.assertAllFlowFilesTransferred(SparqlAskRelationships.FALSE);
	}

	private InputStream readPeopleData() {
		return getClass().getClassLoader().getResourceAsStream("people_data.nq");
	}
}