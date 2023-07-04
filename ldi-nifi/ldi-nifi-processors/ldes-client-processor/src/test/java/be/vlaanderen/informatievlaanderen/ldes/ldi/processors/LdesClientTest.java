package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorRelationships.DATA_RELATIONSHIP;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = 10101)
class LdesClientTest {

	private TestRunner testRunner;

	@BeforeEach
	public void init() {
		testRunner = TestRunners.newTestRunner(LdesClient.class);
	}

	@AfterEach
	void tearDown() {
		((LdesClient) testRunner.getProcessor()).onRemoved();
	}

	@ParameterizedTest
	@ArgumentsSource(MatchNumberOfFlowFilesArgumentsProvider.class)
	void shouldMatchNumberOfFlowFiles(String dataSourceUrl, int numberOfRuns) {
		testRunner.setProperty("DATA_SOURCE_URL", dataSourceUrl);
		testRunner.setProperty("STATE_PERSISTENCE_STRATEGY",
				"SQLITE");

		testRunner.setProperty("KEEP_STATE", Boolean.FALSE.toString());

		testRunner.run(numberOfRuns);

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(numberOfRuns, dataFlowfiles.size());
	}

	@ParameterizedTest
	@ArgumentsSource(RequestExecutorProvider.class)
	void shouldSupportDifferentHttpRequestExecutors(Map<String, String> properties) {
		testRunner.setProperty("DATA_SOURCE_URL",
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
		testRunner.setProperty("DATA_SOURCE_URL", "http://localhost:10101/retry");
		testRunner.setProperty("STATE_PERSISTENCE_STRATEGY", "MEMORY");
		testRunner.setProperty("KEEP_STATE", Boolean.FALSE.toString());
		testRunner.setProperty("RETRIES_ENABLED", Boolean.TRUE.toString());
		testRunner.setProperty("MAX_RETRIES", "3");
		testRunner.setProperty("STATUSES_TO_RETRY", "418");

		testRunner.run(6);

		WireMock.verify(2, getRequestedFor(urlEqualTo("/retry")));

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(6, dataFlowfiles.size());
	}

	@Test
	void when_runningLdesClientWithStreamPropertiesFlags_expectsLdesPropertiesInFlowFile() {
		testRunner.setProperty("DATA_SOURCE_URL",
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

	static class RequestExecutorProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(Map.of("AUTHORIZATION_STRATEGY", "NO_AUTH")),
					Arguments.of(Map.of("AUTHORIZATION_STRATEGY", "API_KEY", "API_KEY_HEADER_PROPERTY", "header",
							"API_KEY_PROPERTY", "key")),
					Arguments.of(Map.of("AUTHORIZATION_STRATEGY", "OAUTH2_CLIENT_CREDENTIALS",
							"OAUTH_CLIENT_ID", "clientId", "OAUTH_CLIENT_SECRET", "secret",
							"OAUTH_TOKEN_ENDPOINT", "http://localhost:10101/token")));
		}
	}
}
