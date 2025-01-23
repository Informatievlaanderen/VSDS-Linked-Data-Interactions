package be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SqlitePropertiesTest {
	private static final String INSTANCE_NAME = "testInstance";
	private static final SqliteProperties sqliteProperties = new SqliteProperties(INSTANCE_NAME, true);


	@Test
	void test_InstanceName() {
		String result = sqliteProperties.getInstanceName();

		assertThat(result).isEqualTo(INSTANCE_NAME);
	}

	@Test
	void test_DatabaseDirectory() {
		String databaseDirectory = "testDirectory";
		SqliteProperties customDbDirectorySqliteProperties = new SqliteProperties(databaseDirectory, INSTANCE_NAME, true);

		String result = customDbDirectorySqliteProperties.getDatabaseDirectory();

		assertThat(result).isEqualTo(databaseDirectory);
	}

	@Test
	void test_DatabaseName() {
		String result = sqliteProperties.getDatabaseName();

		assertThat(result).isEqualTo(INSTANCE_NAME + ".db");
	}

	@Test
	void test_GetProperties() {
		Map<String, String> result = sqliteProperties.getProperties();

		assertThat(result)
				.containsEntry("javax.persistence.jdbc.url", "jdbc:sqlite:././" + INSTANCE_NAME + ".db")
				.containsEntry(SqliteProperties.HIBERNATE_HBM_2_DDL_AUTO, SqliteProperties.UPDATE);
	}
}
