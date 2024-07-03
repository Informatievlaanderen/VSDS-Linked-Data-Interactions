package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PersistenceProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;

import java.util.Map;

public class StatePersistenceFactory {

	public static final StatePersistenceStrategy DEFAULT_STATE_PERSISTENCE_STRATEGY = StatePersistenceStrategy.MEMORY;
	public static final boolean DEFAULT_KEEP_STATE = false;

	public StatePersistence getStatePersistence(ComponentProperties properties) {
		StatePersistenceStrategy state = properties.getOptionalProperty(PersistenceProperties.STATE)
				.flatMap(StatePersistenceStrategy::from)
				.orElse(DEFAULT_STATE_PERSISTENCE_STRATEGY);
		HibernateProperties hibernateProperties = switch (state) {
			case POSTGRES -> createPostgresProperties(properties);
			case SQLITE -> createSqliteProperties(properties);
			default -> Map::of;
		};
		return StatePersistence.from(state, hibernateProperties, properties.getPipelineName());
	}

	private PostgresProperties createPostgresProperties(ComponentProperties properties) {
		String url = properties.getProperty(PersistenceProperties.POSTGRES_URL);
		String username = properties.getProperty(PersistenceProperties.POSTGRES_USERNAME);
		String password = properties.getProperty(PersistenceProperties.POSTGRES_PASSWORD);
		boolean keepState = properties.getOptionalBoolean(PersistenceProperties.KEEP_STATE)
				.orElse(DEFAULT_KEEP_STATE);
		return new PostgresProperties(url, username, password, keepState);
	}

	private SqliteProperties createSqliteProperties(ComponentProperties properties) {
		final String pipelineName = properties.getPipelineName();
		boolean keepState = properties.getOptionalBoolean(PersistenceProperties.KEEP_STATE)
				.orElse(DEFAULT_KEEP_STATE);
		return properties.getOptionalProperty(PersistenceProperties.SQLITE_DIRECTORY)
				.map(directory -> new SqliteProperties(directory, pipelineName, keepState))
				.orElseGet(() -> new SqliteProperties(pipelineName, keepState));
	}
}
