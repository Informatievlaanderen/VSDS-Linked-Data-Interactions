package be.vlaanderen.informatievlaanderen.ldes.ldi;

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
import java.util.stream.Stream;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdesProcessorRelationships.DATA_RELATIONSHIP;
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
		((LdesClient) testRunner.getProcessor()).ldesService.getStateManager().destroyState();
	}

	@ParameterizedTest
	@ArgumentsSource(MatchNumberOfFlowFilesArgumentsProvider.class)
	void shouldMatchNumberOfFlowFiles(String dataSourceUrl, int numberOfRuns, int expectedFlowSize) {
		testRunner.setProperty("DATA_SOURCE_URL", dataSourceUrl);

		testRunner.run(numberOfRuns);

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(expectedFlowSize, dataFlowfiles.size());
	}

	@Test
	void when_runningLdesClientWithStreamPropertiesFlags_expectsLdesPropertiesInFlowFile() {
		testRunner.setProperty("DATA_SOURCE_URL", "http://localhost:10101/exampleData?scenario=gml-data");
		testRunner.setProperty("STREAM_SHAPE_PROPERTY", Boolean.TRUE.toString());

		testRunner.run(1);

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(1, dataFlowfiles.size());
		MockFlowFile flowFile = dataFlowfiles.get(0);
		assertEquals("localhost:10101/exampleData/shape", flowFile.getAttribute("ldes.shacleshapes"));
		assertEquals("http://www.w3.org/ns/prov#generatedAtTime", flowFile.getAttribute("ldes.timestamppath"));
		assertEquals("http://purl.org/dc/terms/isVersionOf", flowFile.getAttribute("ldes.isversionofpath"));

	}

	static class MatchNumberOfFlowFilesArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(
							Named.of("when_runningLdesClientWithConnectedFragments_expectsAllLdesMembers",
									"http://localhost:10101/exampleData?generatedAtTime=2022-05-04T00:00:00.000Z"),
							10, 6),
					Arguments.of(
							Named.of("when_runningLdesClientWithFragmentContaining2DifferentLDES_expectsAllLdesMembers",
									"http://localhost:10101/exampleData?scenario=differentLdes"),
							10, 2),
					Arguments.of(Named.of("when_runningLdesClientWithFragmentContainingGMLData_expectsAllLdesMembers",
							"http://localhost:10101/exampleData?scenario=gml-data"), 1, 1));
		}
	}
}
