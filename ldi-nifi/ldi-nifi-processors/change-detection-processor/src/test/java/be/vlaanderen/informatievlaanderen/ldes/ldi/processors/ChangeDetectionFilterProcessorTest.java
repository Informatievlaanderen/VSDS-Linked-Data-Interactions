package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.dbcp.DBCPConnectionPool;
import org.apache.nifi.dbcp.utils.DBCPProperties;
import org.apache.nifi.reporting.InitializationException;
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
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties.*;
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
	void test_Filter(Map<PropertyDescriptor, String> properties) throws InitializationException {
		properties.forEach(testRunner::setProperty);

		setupDBCPforState(properties.get(STATE_PERSISTENCE_STRATEGY));

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

	private void setupDBCPforState(String state) throws InitializationException {
		// Initialize the DBCPService with H2 in-memory database
		DBCPConnectionPool dbcpService;

		switch (state) {
			case "POSTGRES":
				dbcpService = new DBCPConnectionPool();
				testRunner.addControllerService("dbcpService", dbcpService);
				// Set the DBCPService properties for Postgres database
				testRunner.setProperty(dbcpService, DBCPProperties.DATABASE_URL, postgreSQLContainer.getJdbcUrl());
				testRunner.setProperty(dbcpService, DBCPProperties.DB_USER, postgreSQLContainer.getUsername());
				testRunner.setProperty(dbcpService, DBCPProperties.DB_PASSWORD, postgreSQLContainer.getPassword());
				testRunner.setProperty(dbcpService, DBCPProperties.DB_DRIVERNAME, "org.postgresql.Driver");
				testRunner.enableControllerService(dbcpService);
				testRunner.setProperty(DBCP_SERVICE, "dbcpService");
				break;
			case "SQLITE":
				dbcpService = new DBCPConnectionPool();
				testRunner.addControllerService("dbcpService", dbcpService);
				// Set the DBCPService properties for SQLite database
				testRunner.setProperty(dbcpService, DBCPProperties.DATABASE_URL, "jdbc:sqlite:./test.db");
				testRunner.setProperty(dbcpService, DBCPProperties.DB_DRIVERNAME, "org.sqlite.JDBC");
				testRunner.enableControllerService(dbcpService);
				testRunner.setProperty(DBCP_SERVICE, "dbcpService");
				break;
		}
	}

	static class PropertiesProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Map.of(STATE_PERSISTENCE_STRATEGY, "MEMORY",
							KEEP_STATE, "false"),
					Map.of(STATE_PERSISTENCE_STRATEGY, "SQLITE",
							KEEP_STATE, "false"),
					Map.of(STATE_PERSISTENCE_STRATEGY, "POSTGRES",
							KEEP_STATE, "false")
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