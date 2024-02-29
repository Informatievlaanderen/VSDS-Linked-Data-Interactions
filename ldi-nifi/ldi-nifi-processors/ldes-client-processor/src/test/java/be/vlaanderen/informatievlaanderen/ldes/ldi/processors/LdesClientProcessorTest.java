package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.opentest4j.AssertionFailedError;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorRelationships.DATA_RELATIONSHIP;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = 10101)
class LdesClientProcessorTest {

	private static final String VERSION_OF = "http://purl.org/dc/terms/isVersionOf";

	private TestRunner testRunner;

	private static PostgreSQLContainer<?> postgreSQLContainer;

	@BeforeAll
	static void beforeAll() {
		postgreSQLContainer = new PostgreSQLContainer<>("postgres:11.1")
				.withDatabaseName("integration-test-client-persistence")
				.withUsername("sa")
				.withPassword("sa");
		postgreSQLContainer.start();
	}

	@AfterAll
	static void afterAll() {
		postgreSQLContainer.stop();
	}

	@BeforeEach
	public void init() {
		testRunner = TestRunners.newTestRunner(LdesClientProcessor.class);
	}

	@AfterEach
	void tearDown() {
		((LdesClientProcessor) testRunner.getProcessor()).onRemoved();
	}

	@ParameterizedTest
	@ArgumentsSource(MatchNumberOfFlowFilesArgumentsProvider.class)
	void shouldMatchNumberOfFlowFiles(String dataSourceUrl, int numberOfRuns) {
		testRunner.setProperty("DATA_SOURCE_URLS", dataSourceUrl);
		testRunner.setProperty("STATE_PERSISTENCE_STRATEGY",
				"SQLITE");

		testRunner.setProperty("KEEP_STATE", Boolean.FALSE.toString());

		testRunner.run(numberOfRuns);

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(numberOfRuns, dataFlowfiles.size());
	}

	@ParameterizedTest
	@ArgumentsSource(StatePersistenceArgumentsProvider.class)
	void when_NecessaryPropertiesAreSet_then_statePersistenceCanBeCreated(Map<String, String> properties) {
		testRunner.setProperty("DATA_SOURCE_URLS",
				"http://localhost:10101/exampleData?generatedAtTime=2022-05-03T00:00:00.000Z");
		properties.forEach((key, value) -> testRunner.setProperty(key, value));

		testRunner.setProperty("KEEP_STATE", Boolean.FALSE.toString());

		testRunner.run();

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(1, dataFlowfiles.size());
		List<RDFNode> result = RDFParser
				.fromString(dataFlowfiles.get(0).getContent())
				.lang(Lang.NQUADS)
				.toModel()
				.listObjectsOfProperty(createProperty(VERSION_OF))
				.toList();
		assertEquals(1, result.size());
	}

	@Test
	void shouldSupportRedirectLogic() {
		testRunner.setProperty("AUTHORIZATION_STRATEGY", "NO_AUTH");
		testRunner.setProperty("DATA_SOURCE_URLS", "http://localhost:10101/200-response-with-indirect-url");
		testRunner.setProperty("STATE_PERSISTENCE_STRATEGY", "MEMORY");
		testRunner.setProperty("KEEP_STATE", Boolean.FALSE.toString());
		testRunner.setProperty("STREAM_TIMESTAMP_PATH_PROPERTY", Boolean.FALSE.toString());
		testRunner.setProperty("STREAM_VERSION_OF_PROPERTY", Boolean.FALSE.toString());
		testRunner.setProperty("DATA_SOURCE_FORMAT", "application/ld+json");

		testRunner.run();

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(1, dataFlowfiles.size());
	}

	@Test
	void shouldBeAbleToEndGracefully() {
		// This is an immutable fragment with 1 member and no relations. We reach the
		// end of the ldes after 1 run.
		testRunner.setProperty("DATA_SOURCE_URLS",
				"http://localhost:10101/end-of-ldes?generatedAtTime=2022-05-03T00:00:00.000Z");
		testRunner.setProperty("AUTHORIZATION_STRATEGY", "NO_AUTH");
		testRunner.setProperty("STATE_PERSISTENCE_STRATEGY", "MEMORY");
		testRunner.setProperty("KEEP_STATE", Boolean.FALSE.toString());
		testRunner.setProperty("STREAM_TIMESTAMP_PATH_PROPERTY", Boolean.FALSE.toString());
		testRunner.setProperty("STREAM_VERSION_OF_PROPERTY", Boolean.FALSE.toString());
		testRunner.setProperty("DATA_SOURCE_FORMAT", "application/ld+json");

		testRunner.run(5);

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(1, dataFlowfiles.size());
	}

	@ParameterizedTest
	@ArgumentsSource(RequestExecutorProvider.class)
	void shouldSupportDifferentHttpRequestExecutors(Map<String, String> properties) {
		testRunner.setProperty("DATA_SOURCE_URLS",
				"http://localhost:10101/exampleData?generatedAtTime=2022-05-03T00:00:00.000Z");
		testRunner.setProperty("STATE_PERSISTENCE_STRATEGY", "MEMORY");
		testRunner.setProperty("KEEP_STATE", Boolean.FALSE.toString());
		properties.forEach((key, value) -> testRunner.setProperty(key, value));

		testRunner.run(6);

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(6, dataFlowfiles.size());
	}

	@Test
	void shouldSupportRetry() {
		testRunner.setProperty("DATA_SOURCE_URLS", "http://localhost:10101/retry");
		testRunner.setProperty("STATE_PERSISTENCE_STRATEGY", "MEMORY");
		testRunner.setProperty("KEEP_STATE", Boolean.FALSE.toString());
		testRunner.setProperty("RETRIES_ENABLED", Boolean.TRUE.toString());
		testRunner.setProperty("MAX_RETRIES", "3");
		testRunner.setProperty("STATUSES_TO_RETRY", "418");

		testRunner.run(6);

		WireMock.verify(3, getRequestedFor(urlEqualTo("/retry")));

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(6, dataFlowfiles.size());
	}

	@Test
	void shouldSupportVersionMaterialisation() {
		testRunner.setProperty("DATA_SOURCE_URLS",
				"http://localhost:10101/exampleData?generatedAtTime=2022-05-03T00:00:00.000Z");
		testRunner.setProperty("STATE_PERSISTENCE_STRATEGY", "MEMORY");
		testRunner.setProperty("KEEP_STATE", Boolean.FALSE.toString());
		testRunner.setProperty("USE_VERSION_MATERIALISATION", Boolean.TRUE.toString());
		testRunner.setProperty("RESTRICT_TO_MEMBERS", Boolean.FALSE.toString());
		testRunner.setProperty("VERSION_OF_PROPERTY", VERSION_OF);

		testRunner.run();

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(1, dataFlowfiles.size());

		List<RDFNode> result = RDFParser
				.fromString(dataFlowfiles.get(0).getContent())
				.lang(Lang.NQUADS)
				.toModel()
				.listObjectsOfProperty(createProperty("http://purl.org/dc/terms/isVersionOf"))
				.toList();
		assertEquals(0, result.size());
	}

	@Test
	void when_runningLdesClientWithStreamPropertiesFlags_expectsLdesPropertiesInFlowFile() {
		testRunner.setProperty("DATA_SOURCE_URLS",
				"http://localhost:10101/exampleData?scenario=gml-data");
		testRunner.setProperty("STREAM_SHAPE_PROPERTY", Boolean.TRUE.toString());

		testRunner.run();

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(1, dataFlowfiles.size());
		MockFlowFile flowFile = dataFlowfiles.get(0);
		assertEquals("localhost:10101/exampleData/shape",
				flowFile.getAttribute("ldes.shacleshapes"));
		assertEquals("http://www.w3.org/ns/prov#generatedAtTime",
				flowFile.getAttribute("ldes.timestamppath"));
		assertEquals("http://purl.org/dc/terms/isVersionOf",
				flowFile.getAttribute("ldes.isversionofpath"));

	}

	@Test
	void when_DataSourceUrlsIsAbsent_then_ThrowException() {
		testRunner.setProperty("DATA_SOURCE_ENDPOINTS", "http://some-server.com/ldes");

		assertThatThrownBy(() -> testRunner.run())
				.isInstanceOf(AssertionFailedError.class)
				.hasMessageStartingWith("Processor has 2 validation failures:");
	}

	@Test
	void when_DataSourceUrlsIsEmpty_then_ThrowException() {
		testRunner.setProperty("DATA_SOURCE_URLS", "");

		assertThatThrownBy(() -> testRunner.run())
				.isInstanceOf(AssertionFailedError.class)
				.hasMessageStartingWith("Processor has 1 validation failures:");
	}

	static class MatchNumberOfFlowFilesArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(
							Named.of("when_runningLdesClientWithConnectedFragments_expectsAllLdesMembers",
									"http://localhost:10101/exampleData?generatedAtTime=2022-05-03T00:00:00.000Z"),
							6));
		}
	}

	static class StatePersistenceArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(Map.of("STATE_PERSISTENCE_STRATEGY", "MEMORY")),
					Arguments.of(Map.of("STATE_PERSISTENCE_STRATEGY", "FILE")),
					Arguments.of(Map.of("STATE_PERSISTENCE_STRATEGY", "SQLITE")),
					Arguments.of(Map.of("STATE_PERSISTENCE_STRATEGY", "POSTGRES", "POSTGRES_URL",
							postgreSQLContainer.getJdbcUrl(), "POSTGRES_USERNAME", postgreSQLContainer.getUsername(),
							"POSTGRES_PASSWORD", postgreSQLContainer.getPassword())));
		}
	}

	static class RequestExecutorProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(Map.of("AUTHORIZATION_STRATEGY", "NO_AUTH")),
					Arguments.of(Map.of("AUTHORIZATION_STRATEGY", "API_KEY", "API_KEY_HEADER_PROPERTY", "header",
							"API_KEY_PROPERTY", "key")),
					Arguments.of(Map.of("AUTHORIZATION_STRATEGY", "OAUTH2_CLIENT_CREDENTIALS",
							"OAUTH_CLIENT_ID", "clientId", "OAUTH_CLIENT_SECRET", "secret",
							"OAUTH_TOKEN_ENDPOINT", "http://localhost:10101/token", "OAUTH_SCOPE", "default")));
		}
	}
}
