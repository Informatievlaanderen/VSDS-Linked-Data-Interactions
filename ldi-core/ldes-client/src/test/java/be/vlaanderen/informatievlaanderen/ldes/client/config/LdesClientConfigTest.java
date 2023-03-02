package be.vlaanderen.informatievlaanderen.ldes.client.config;

import be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults;
import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.LdesConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LdesClientConfigTest {

	private final String propertiesConfig = "application-config-test.properties";
	private final String propertiesApiKeyHeader = "api_key_header";
	private final String propertiesApiKey = "api_key";
	private final String propertiesStrategy = "persistence_strategy";
	private final String propertiesDbDriverClassName = "db_driver_class_name";
	private final String propertiesDbName = "db_name";
	private final String propertiesDbUrl = "db_url";
	private final String propertiesDbUsername = "db_username";
	private final String propertiesDbPassword = "db_password";

	@Test
	void whenDefaultConfig_thenConfigHasExpectedValues() {
		LdesClientConfig config = new LdesClientConfig();

		assertEquals(LdesClientDefaults.DEFAULT_API_KEY_HEADER, config.getApiKeyHeader());
		assertTrue(config.getApiKey().isEmpty());
		assertEquals(LdesClientDefaults.DEFAULT_PERSISTENCE_STRATEGY, config.getPersistenceStrategy());
		assertEquals(LdesClientDefaults.DEFAULT_PERSISTENCE_DB_DRIVER, config.getPersistenceDbDriver());
		assertEquals(LdesClientDefaults.DEFAULT_PERSISTENCE_DB_NAME, config.getPersistenceDbName());
		assertEquals(LdesClientDefaults.DEFAULT_PERSISTENCE_DB_URL, config.getPersistenceDbUrl());
		assertNull(config.getPersistenceDbUsername());
		assertNull(config.getPersistenceDbPassword());
	}

	@Test
	void whenConfigLoadedFromProperties_thenConfigHasExpectedValues() {
		LdesClientConfig config = new LdesClientConfig(propertiesConfig);

		assertEquals(propertiesApiKeyHeader, config.getApiKeyHeader());
		assertEquals(propertiesApiKey, config.getApiKey());
		assertEquals(propertiesStrategy, config.getPersistenceStrategy());
		assertEquals(propertiesDbDriverClassName, config.getPersistenceDbDriver());
		assertEquals(propertiesDbName, config.getPersistenceDbName());
		assertEquals(propertiesDbUrl, config.getPersistenceDbUrl());
		assertEquals(propertiesDbUsername, config.getPersistenceDbUsername());
		assertEquals(propertiesDbPassword, config.getPersistenceDbPassword());
	}

	@Test
	void whenInvalidPropertiesFileSpecified_thenConfigThrowsLdesConfigurationException() {
		String invalidPropertiesFile = "INVALID";

		LdesConfigurationException ex = assertThrows(LdesConfigurationException.class,
				() -> new LdesClientConfig(invalidPropertiesFile));

		assertEquals("Unable to process configuration properties (FILE: " + invalidPropertiesFile + ")",
				ex.getMessage());
	}
}
