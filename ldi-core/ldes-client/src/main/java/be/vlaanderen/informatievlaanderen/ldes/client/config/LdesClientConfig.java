package be.vlaanderen.informatievlaanderen.ldes.client.config;

import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.LdesConfigurationException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_API_KEY_HEADER;
import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_CONFIGURATION_PROPERTIES;
import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_PERSISTENCE_DB_DRIVER;
import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_PERSISTENCE_DB_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_PERSISTENCE_DB_URL;
import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_PERSISTENCE_STRATEGY;

public class LdesClientConfig {

	private final PropertiesConfiguration config = new PropertiesConfiguration();

	protected static final String KEY_API_KEY_HEADER = "vsds.client.apikey.header";
	protected static final String KEY_API_KEY = "vsds.client.apikey.key";
	protected static final String KEY_STRATEGY = "vsds.client.persistence.strategy";
	protected static final String KEY_DB_DRIVER = "vsds.client.persistence.db.driver-class-name";
	protected static final String KEY_DB_NAME = "vsds.client.persistence.db.name";
	protected static final String KEY_DB_URL = "vsds.client.persistence.db.url";
	protected static final String KEY_DB_USERNAME = "vsds.client.persistence.db.username";
	protected static final String KEY_DB_PASSWORD = "vsds.client.persistence.db.password";

	protected String apiKeyHeader;
	protected String apiKey;
	protected String strategy;
	protected String persistenceDbDriver;
	protected String persistenceDbName;
	protected String persistenceDbUrl;
	protected String persistenceDbUsername;
	protected String persistenceDbPassword;

	public LdesClientConfig() {
		this(DEFAULT_CONFIGURATION_PROPERTIES);
	}

	public LdesClientConfig(String configurationPropertiesFile) {
		loadConfig(configurationPropertiesFile);
	}

	private void loadConfig(String configurationPropertiesFile) {
		try {
			config.load(configurationPropertiesFile);

			apiKeyHeader = config.getString(KEY_API_KEY_HEADER, DEFAULT_API_KEY_HEADER);
			apiKey = config.getString(KEY_API_KEY, null);
			strategy = config.getString(KEY_STRATEGY, DEFAULT_PERSISTENCE_STRATEGY);
			persistenceDbDriver = config.getString(KEY_DB_DRIVER, DEFAULT_PERSISTENCE_DB_DRIVER);
			persistenceDbName = config.getString(KEY_DB_NAME, DEFAULT_PERSISTENCE_DB_NAME);
			persistenceDbUrl = config.getString(KEY_DB_URL, DEFAULT_PERSISTENCE_DB_URL);
			persistenceDbUsername = config.getString(KEY_DB_USERNAME, null);
			persistenceDbPassword = config.getString(KEY_DB_PASSWORD, null);
		} catch (ConfigurationException e) {
			throw new LdesConfigurationException("Unable to process configuration properties",
					configurationPropertiesFile, e);
		}
	}

	public String getApiKeyHeader() {
		return apiKeyHeader;
	}

	public void setApiKeyHeader(String apiKeyHeader) {
		this.apiKeyHeader = apiKeyHeader;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public boolean hasApiKey() {
		return getApiKey() != null;
	}

	public String getPersistenceStrategy() {
		return strategy;
	}

	public void setPersistenceStrategy(String strategy) {
		this.strategy = strategy;
	}

	public String getPersistenceDbDriver() {
		return persistenceDbDriver;
	}

	public void setPersistenceDbDriver(String persistenceDbDriver) {
		this.persistenceDbDriver = persistenceDbDriver;
	}

	public String getPersistenceDbName() {
		return persistenceDbName;
	}

	public void setPersistenceDbName(String persistenceDbName) {
		this.persistenceDbName = persistenceDbName;
	}

	public String getPersistenceDbUrl() {
		return persistenceDbUrl;
	}

	public void setPersistenceDbUrl(String persistenceDbUrl) {
		this.persistenceDbUrl = persistenceDbUrl;
	}

	public String getPersistenceDbUsername() {
		return persistenceDbUsername;
	}

	public void setPersistenceDbUsername(String persistenceDbUsername) {
		this.persistenceDbUsername = persistenceDbUsername;
	}

	public String getPersistenceDbPassword() {
		return persistenceDbPassword;
	}

	public void setPersistenceDbPassword(String persistenceDbPassword) {
		this.persistenceDbPassword = persistenceDbPassword;
	}
}
