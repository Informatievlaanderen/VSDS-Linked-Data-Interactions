package be.vlaanderen.informatievlaanderen.ldes.client;

import org.apache.jena.riot.Lang;

public class LdesClientDefaults {

	private LdesClientDefaults() {
	}

	public static final String PERSISTENCE_STRATEGY_NONE = "non-persisted";
	public static final String PERSISTENCE_STRATEGY_SQLITE = "sqlite";

	public static final String DEFAULT_CONFIGURATION_PROPERTIES = "application.properties";

	/**
	 * The expected RDF format of the LDES data source
	 */
	public static final Lang DEFAULT_DATA_SOURCE_FORMAT = Lang.JSONLD;
	/**
	 * The desired RDF format for output
	 */
	public static final Lang DEFAULT_DATA_DESTINATION_FORMAT = Lang.NQUADS;

	/**
	 * The number of seconds to add to the current time before a fragment is
	 * considered expired.
	 * <p>
	 * Only used when the HTTP request that contains the fragment does not have a
	 * max-age element in the Cache-control header.
	 */
	public static final Long DEFAULT_FRAGMENT_EXPIRATION_INTERVAL = 604800L;

	/**
	 * The amount of time to wait to call the LdesService when the queue has no
	 * mutable fragments left or when the mutable fragments have not yet expired.
	 */
	public static final Long DEFAULT_POLLING_INTERVAL = 60L;

	public static final String DEFAULT_API_KEY_HEADER = "X-API-KEY";
	public static final String DEFAULT_PERSISTENCE_STRATEGY = PERSISTENCE_STRATEGY_SQLITE;
	public static final String DEFAULT_PERSISTENCE_DB_DRIVER = "org.sqlite.JDBC";
	public static final String DEFAULT_PERSISTENCE_DB_NAME = "ldesclientstate.db";
	public static final String DEFAULT_PERSISTENCE_DB_URL = "jdbc:sqlite:ldesclientstate.db";
}
