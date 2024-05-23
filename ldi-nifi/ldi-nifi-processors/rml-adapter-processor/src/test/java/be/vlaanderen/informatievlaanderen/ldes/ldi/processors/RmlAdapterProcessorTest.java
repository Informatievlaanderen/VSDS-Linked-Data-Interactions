package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties.DATA_DESTINATION_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RmlAdapterProperties.RML_MAPPING_CONTENT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RmlAdapterProperties.RML_MAPPING_FILE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RmlAdapterProcessorTest {
	public static final Lang DESTINATION_FORMAT = Lang.NQUADS;
	private TestRunner testRunner;

	@BeforeEach
	void setUp() {
		testRunner = TestRunners.newTestRunner(RmlAdapterProcessor.class);
		testRunner.setProperty(DATA_DESTINATION_FORMAT, DESTINATION_FORMAT.getHeaderString());
	}

	private static Stream<Arguments> provideMappings() {
		return Stream.of(
				Arguments.of("smartphones/mapping.ttl", "smartphones/data.json", 5),
				Arguments.of("usercarts/mapping.ttl", "usercarts/data.json", 20)
		);
	}

	@ParameterizedTest
	@MethodSource("provideMappings")
	void test_mappings(String mappingFileName, String dataFileName, int expectedSuccessCount) {
		testRunner.setProperty(RML_MAPPING_CONTENT, readFileContent(mappingFileName));
		final String data = readFileContent(dataFileName);

		testRunner.enqueue(data, Map.of("mime.type", "application/json"));
		testRunner.run();

		testRunner.assertQueueEmpty();
		testRunner.assertTransferCount(SUCCESS, expectedSuccessCount);
		testRunner.assertTransferCount(FAILURE, 0);
	}

	@Test
	void test_awv_location() {
		final Model expected = RDFParser.source("awv/location/expected.nt").lang(DESTINATION_FORMAT).toModel();
		final String data = readFileContent("awv/location/data.xml");
		testRunner.setProperty(RML_MAPPING_FILE, "src/test/resources/awv/location/mapping.ttl");

		testRunner.enqueue(data, Map.of("mime.type", "application/xml"));
		testRunner.run();

		final List<MockFlowFile> flowFiles = testRunner.getFlowFilesForRelationship(SUCCESS);
		final Model result = RDFParser.fromString(flowFiles.getFirst().getContent()).lang(DESTINATION_FORMAT).toModel();

		testRunner.assertQueueEmpty();
		assertThat(flowFiles).hasSize(1);
		assertThat(result).matches(expected::isIsomorphicWith);
	}

	@Test
	void given_BothRmlMappingContentAndFileAreSet_when_RunProcessor_then_ThrowException() {
		testRunner.setProperty(RML_MAPPING_FILE, "src/test/resources/awv/location/mapping.ttl");
		testRunner.setProperty(RML_MAPPING_CONTENT, readFileContent("awv/location/mapping.ttl"));

		assertThatThrownBy(() -> testRunner.run())
				.isInstanceOf(AssertionFailedError.class)
				.hasMessageContaining("both RML mapping content and RML mapping file cannot be set at the same time");
	}

	@Test
	void given_NeitherRmlMappingContentAndFileAreSet_when_RunProcessor_then_ThrowException() {
		assertThatThrownBy(() -> testRunner.run())
				.isInstanceOf(AssertionFailedError.class)
				.hasMessageContaining("either RML mapping content or RML mapping file must be set");
	}

	private String readFileContent(String fileName) {
		try {
			final ClassLoader classLoader = getClass().getClassLoader();
			final File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
			return FileUtils.readFileToString(file, "UTF-8");
		} catch (NullPointerException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}