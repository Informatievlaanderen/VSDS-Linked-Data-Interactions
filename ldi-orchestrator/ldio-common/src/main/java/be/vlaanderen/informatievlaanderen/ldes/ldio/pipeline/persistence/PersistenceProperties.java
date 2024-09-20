package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence;

/**
 * LDIO properties for managing the persistence
 */
public class PersistenceProperties {
	private PersistenceProperties() {}

	public static final String STATE = "state";
	public static final String KEEP_STATE = "keep-state";
	public static final String POSTGRES_USERNAME = "postgres.username";
	public static final String POSTGRES_PASSWORD = "postgres.password";
	public static final String POSTGRES_URL = "postgres.url";
}
