package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFWriter;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;

import java.io.File;
import java.io.IOException;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

class RDF4JRepositoryMaterialisationProcessorTest {
	private static final String REPOSITORY_ID = "test";
	private static final int EXPOSED_PORT = 8080;
	private static GenericContainer<?> rdf4jContainer;
	private static String sparqlHost;
	private TestRunner testRunner;

	@BeforeAll
	static void beforeAll() {
		rdf4jContainer = new GenericContainer<>("eclipse/rdf4j-workbench:4.3.4")
				.withExposedPorts(EXPOSED_PORT);
		rdf4jContainer.start();

		sparqlHost = "http://%s:%d/rdf4j-server".formatted(rdf4jContainer.getHost(), rdf4jContainer.getMappedPort(EXPOSED_PORT));

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			final HttpPut httpPut = new HttpPut(sparqlHost + "/repositories/" + REPOSITORY_ID);
			httpPut.setHeader("Content-Type", "text/turtle");
			File requestBodyFile = new File("src/test/resources/repo-definition.ttl");
			httpPut.setEntity(new FileEntity(requestBodyFile));

			final HttpResponse response = httpClient.execute(httpPut);

			assertThat(response.getStatusLine().getStatusCode())
					.as("Initialization of the repository should be successful")
					.isEqualTo(204);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	static void afterAll() {
		rdf4jContainer.stop();
	}

	@BeforeEach
	void setUp() {
		testRunner = TestRunners.newTestRunner(RDF4JRepositoryMaterialisationProcessor.class);
	}

	@AfterEach
	void tearDown() {
		((RDF4JRepositoryMaterialisationProcessor) testRunner.getProcessor()).onRemoved();
	}

	@Test
	void given_ValidConfig_when_ProcessValidModel_then_SuccessFlowFilesAreNotEmpty() {
		final Model inputModel = RDFDataMgr.loadModel("people_data.nq");
		final String inputString = RDFWriter.source(inputModel).lang(Lang.NQ).asString();

		testRunner.setProperty("SPARQL_HOST", sparqlHost);
		testRunner.setProperty("REPOSITORY_ID", REPOSITORY_ID);
		testRunner.enqueue(inputString);
		testRunner.run();

		assertThat(testRunner.getFlowFilesForRelationship(SUCCESS)).isNotEmpty();
		assertThat(testRunner.getFlowFilesForRelationship(FAILURE)).isEmpty();
	}

	@Test
	void given_InvalidConfig_when_ProcessValidModel_then_FailureFlowFilesAreNotEmpty() {
		final Model inputModel = RDFDataMgr.loadModel("people_data.nq");
		final String inputString = RDFWriter.source(inputModel).lang(Lang.NQ).asString();

		testRunner.setProperty("REPOSITORY_ID", "non-existing-id");

		testRunner.enqueue(inputString);
		testRunner.run();

		assertThat(testRunner.getFlowFilesForRelationship(SUCCESS)).isEmpty();
		assertThat(testRunner.getFlowFilesForRelationship(FAILURE)).isNotEmpty();
	}

	@Test
	void given_ValidConfig_when_ProcessInvalidModel_then_FailureFlowFilesAreNotEmpty() {
		testRunner.setProperty("SPARQL_HOST", sparqlHost);
		testRunner.setProperty("REPOSITORY_ID", REPOSITORY_ID);
		testRunner.enqueue("Random input");
		testRunner.run();

		assertThat(testRunner.getFlowFilesForRelationship(SUCCESS)).isEmpty();
		assertThat(testRunner.getFlowFilesForRelationship(FAILURE)).isNotEmpty();
	}
}