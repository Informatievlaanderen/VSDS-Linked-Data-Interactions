package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ChangeDetectionFilterRelationships.IGNORED;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ChangeDetectionFilterRelationships.NEW_STATE_RECEIVED;
import static org.assertj.core.api.Assertions.assertThat;

class ChangeDetectionFilterProcessorTest {
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
		testRunner = TestRunners.newTestRunner(ChangeDetectionFilterProcessor.class);
	}

	@AfterEach
	void tearDown() {
		((ChangeDetectionFilterProcessor) testRunner.getProcessor()).onRemoved();
	}

	@ParameterizedTest
	@ArgumentsSource(PropertiesProvider.class)
	void test_Filter(Map<String, String> properties) {
		properties.forEach(testRunner::setProperty);

		testRunner.enqueue(createInputStreamForFile("members/state-member.nq"));
		testRunner.enqueue(createInputStreamForFile("members/state-member.ttl"), Map.of("mime.type", "text/turtle"));
		testRunner.enqueue(createInputStreamForFile("members/changed-state-member.nq"));
		testRunner.run(3);

		int uniqueMembers = testRunner.getFlowFilesForRelationship(NEW_STATE_RECEIVED).size();
		int ignoredMembers = testRunner.getFlowFilesForRelationship(IGNORED).size();

		assertThat(uniqueMembers)
				.as("Number of unique members that should have been processed")
				.isEqualTo(2);
		assertThat(ignoredMembers)
				.as("Number of members that should have been ignored")
				.isEqualTo(1);
	}

	static class PropertiesProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Map.of("STATE_PERSISTENCE_STRATEGY", "MEMORY", "KEEP_STATE", "false"),
					Map.of("STATE_PERSISTENCE_STRATEGY", "SQLITE",
							"SQLITE_DIRECTORY", "change-detection-filter",
							"KEEP_STATE", "false"),
					Map.of("STATE_PERSISTENCE_STRATEGY", "POSTGRES",
							"POSTGRES_URL", postgreSQLContainer.getJdbcUrl(),
							"POSTGRES_USERNAME", postgreSQLContainer.getUsername(),
							"POSTGRES_PASSWORD", postgreSQLContainer.getPassword(),
							"KEEP_STATE", "false")
					).map(Arguments::of);
		}
	}

	private InputStream createInputStreamForFile(String fileName) {
		try {
			final File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).getFile());
			return new FileInputStream(file);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}