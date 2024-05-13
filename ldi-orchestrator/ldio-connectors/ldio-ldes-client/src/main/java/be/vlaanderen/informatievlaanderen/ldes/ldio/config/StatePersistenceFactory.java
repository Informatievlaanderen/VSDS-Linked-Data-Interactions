package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.persistence.PersistenceProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects.StatePersistenceStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres.PostgresProperties;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite.SqliteEntityManagerFactory.DATABASE_DIRECTORY_KEY;

public class StatePersistenceFactory {

	public static final StatePersistenceStrategy DEFAULT_STATE_PERSISTENCE_STRATEGY = StatePersistenceStrategy.MEMORY;
	public static final boolean DEFAULT_KEEP_STATE = false;

	public StatePersistence getStatePersistence(ComponentProperties properties) {
		StatePersistenceStrategy state = properties.getOptionalProperty(PersistenceProperties.STATE)
				.flatMap(StatePersistenceStrategy::from)
				.orElse(DEFAULT_STATE_PERSISTENCE_STRATEGY);
		Map<String, String> persistenceProperties = switch (state) {
			case POSTGRES -> createPostgresProperties(properties);
			case SQLITE -> Map.of(DATABASE_DIRECTORY_KEY, properties.getProperty(PersistenceProperties.SQLITE_DIRECTORY));
			default -> Map.of();
		};
		return StatePersistence.from(state, persistenceProperties, properties.getPipelineName());
	}

	private Map<String, String> createPostgresProperties(ComponentProperties properties) {
		String url = properties.getProperty(PersistenceProperties.POSTGRES_URL);
		String username = properties.getProperty(PersistenceProperties.POSTGRES_USERNAME);
		String password = properties.getProperty(PersistenceProperties.POSTGRES_PASSWORD);
		boolean keepState = properties.getOptionalBoolean(PersistenceProperties.KEEP_STATE)
				.orElse(DEFAULT_KEEP_STATE);
		return new PostgresProperties(url, username, password, keepState).getProperties();
	}
}
