package be.vlaanderen.informatievlaanderen.ldes.ldi.postgres;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresPropertiesTest {

	@Test
	void test_GetProperties() {
		final String url = "jdbc:postgresql://localhost:5432/testdb";
		final String username = "testuser";
		final String password = "testpass";
		final PostgresProperties postgresProperties = new PostgresProperties(url, username, password, true);

		final Map<String, String> result = postgresProperties.getProperties();

		assertThat(result)
				.containsEntry(PostgresProperties.HIBERNATE_CONNECTION_URL, url)
				.containsEntry(PostgresProperties.HIBERNATE_CONNECTION_USERNAME, username)
				.containsEntry(PostgresProperties.HIBERNATE_CONNECTION_PASSWORD, password)
				.containsEntry(PostgresProperties.HIBERNATE_HBM_2_DDL_AUTO, PostgresProperties.UPDATE);
	}
}
