package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.HttpSparqlOutProcessorProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpSparqlOutProcessorTest {
	static final int PORT = 12321;
	private static final Map<PropertyDescriptor, String> minimalProperties = Map.of(
			ENDPOINT, "http://localhost:%s/sparql".formatted(PORT),
			GRAPH, "http://example.graph.com"
	);
	private static final String MIME_TYPE = "mime.type";
	private TestRunner testRunner;

	@BeforeEach
	void setUp() {
		testRunner = TestRunners.newTestRunner(HttpSparqlOutProcessor.class);
	}

	@Test
	void given_InvalidModel_when_SparqlOut_then_AddModelToFailureRelation() {
		minimalProperties.forEach(testRunner::setProperty);

		testRunner.enqueue("invalid model", Map.of(MIME_TYPE, Lang.NT.getHeaderString()));

		testRunner.run();

		assertThat(testRunner.getFlowFilesForRelationship(FAILURE)).hasSize(1);
	}

	@Test
	void test_EmptyFlowFile() {
		minimalProperties.forEach(testRunner::setProperty);

		testRunner.run();

		assertThat(testRunner.getFlowFilesForRelationship(SUCCESS)).isEmpty();
		assertThat(testRunner.getFlowFilesForRelationship(FAILURE)).isEmpty();
	}

	@Nested
	@WireMockTest(httpPort = HttpSparqlOutProcessorTest.PORT)
	class HttpInteractions {
		private static Path path;
		private Model expectedModel;
		private static final Lang mimetype = Lang.TURTLE;

		@BeforeAll
		static void beforeAll() throws URISyntaxException {
			URL resource = HttpSparqlOutProcessorTest.class.getClassLoader().getResource("mob-hind-model.ttl");
			path = Path.of(Objects.requireNonNull(resource).toURI());
		}

		@BeforeEach
		void setUp() {
			expectedModel = RDFParser.source(path).lang(Lang.TURTLE).toModel();
			stubFor(post(urlEqualTo("/sparql")).willReturn(aResponse().withStatus(200)));
		}

		@Test
		void given_ValidModel_when_SparqlOut_then_AddModelToSuccessRelation() throws IOException {
			minimalProperties.forEach(testRunner::setProperty);
			testRunner.setProperty(REPLACEMENT_ENABLED, Boolean.FALSE.toString());

			testRunner.enqueue(path, Map.of(MIME_TYPE, mimetype.getHeaderString()));
			testRunner.run();

			MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(SUCCESS).getFirst();
			Model result = RDFParser.create().source(flowFile.getContentStream()).lang(mimetype).toModel();

			verify(postRequestedFor(urlEqualTo("/sparql")).withRequestBody(containing("INSERT DATA").and(notContaining("DELETE FROM"))));
			assertThat(result).matches(expectedModel::isIsomorphicWith);
		}

		@Test
		void given_SkolemisationEnabled_when_SparqlOut_then_AddModelToSuccessRelation() throws IOException {
			final String skolemDomain = "http://example.com";
			minimalProperties.forEach(testRunner::setProperty);
			testRunner.setProperty(SKOLEMISATION_DOMAIN, skolemDomain);

			testRunner.enqueue(path, Map.of(MIME_TYPE, mimetype.getHeaderString()));
			testRunner.run();

			MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(SUCCESS).getFirst();
			Model result = RDFParser.create().source(flowFile.getContentStream()).lang(mimetype).toModel();

			verify(postRequestedFor(urlEqualTo("/sparql")).withRequestBody(containing("%s/.well-known/genid/".formatted(skolemDomain))));
			assertThat(result).matches(expectedModel::isIsomorphicWith);
		}

		@Test
		void given_GraphOmitted_when_SparqlOut_then_AddModelToSuccessRelation() throws IOException {
			testRunner.setProperty(ENDPOINT, "http://localhost:%s/sparql".formatted(PORT));

			testRunner.enqueue(path, Map.of(MIME_TYPE, mimetype.getHeaderString()));
			testRunner.run();

			MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(SUCCESS).getFirst();
			Model result = RDFParser.create().source(flowFile.getContentStream()).lang(mimetype).toModel();

			verify(postRequestedFor(urlEqualTo("/sparql")).withRequestBody(notContaining("FROM")));
			assertThat(result).matches(expectedModel::isIsomorphicWith);
		}

		@Test
		void given_CustomDeteFunction_when_SparqlOut_then_AddModelToSuccessRelation() throws IOException {
			final String customDeleteFunction = "custom function";
			testRunner.setProperty(ENDPOINT, "http://localhost:%s/sparql".formatted(PORT));
			testRunner.setProperty(REPLACEMENT_DELETE_FUNCTION, customDeleteFunction);

			testRunner.enqueue(path, Map.of(MIME_TYPE, mimetype.getHeaderString()));
			testRunner.run();

			MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(SUCCESS).getFirst();
			Model result = RDFParser.create().source(flowFile.getContentStream()).lang(mimetype).toModel();

			verify(postRequestedFor(urlEqualTo("/sparql")).withRequestBody(containing(customDeleteFunction).and(notContaining("FROM"))));
			assertThat(result).matches(expectedModel::isIsomorphicWith);
		}

		@Test
		void given_EndpointReturns400_when_SparqlOut_then_AddModelToSuccessRelation() throws IOException {
			stubFor(post(urlEqualTo("/sparql-fail")).willReturn(aResponse().withStatus(400)));
			testRunner.setProperty(ENDPOINT, "http://localhost:%s/sparql-fail".formatted(PORT));

			testRunner.enqueue(path, Map.of(MIME_TYPE, mimetype.getHeaderString()));
			testRunner.run();

			MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(FAILURE).getFirst();
			Model result = RDFParser.create().source(flowFile.getContentStream()).lang(mimetype).toModel();

			verify(postRequestedFor(urlEqualTo("/sparql-fail")));
			assertThat(result).matches(expectedModel::isIsomorphicWith);
		}
	}

	@ParameterizedTest
	@MethodSource("invalidProperties")
	void given_InvalidProperties_when_TryToTestRun_then_ThrowAssertionFailedError(Map<PropertyDescriptor, String> properties, int expectedFailures) {
		properties.forEach(testRunner::setProperty);

		assertThatThrownBy(testRunner::run)
				.isInstanceOf(AssertionFailedError.class)
				.hasMessageContaining("%d validation failures", expectedFailures);
	}

	static Stream<Arguments> invalidProperties() {
		return Stream.of(
				Arguments.of(Map.of(), 1),
				Arguments.of(Map.of(GRAPH, "http://example.graph.com"), 1),
				Arguments.of(Map.of(ENDPOINT, "", GRAPH, "http://"), 2),
				Arguments.of(Map.of(ENDPOINT, "http://localhost:8890/sparql", GRAPH, "http://example.graph.com", REPLACEMENT_DEPTH, "ten"), 1),
				Arguments.of(Map.of(ENDPOINT, "http://localhost:8890/sparql", GRAPH, "http://example.graph.com", REPLACEMENT_ENABLED, "off"), 1)
		);
	}
}