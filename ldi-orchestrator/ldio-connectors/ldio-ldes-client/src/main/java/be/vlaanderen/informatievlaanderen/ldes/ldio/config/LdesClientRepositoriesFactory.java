package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateUtil;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.domain.valueobject.LdesClientRepositories;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PersistenceProperties.*;

public class LdesClientRepositoriesFactory {
	public static final StatePersistenceStrategy DEFAULT_STATE_PERSISTENCE_STRATEGY = StatePersistenceStrategy.MEMORY;
	public static final boolean DEFAULT_KEEP_STATE = false;

	private LdesClientRepositoriesFactory() {
	}

	public static LdesClientRepositories getLdesClientRepositories(ComponentProperties properties) {
		StatePersistenceStrategy state = properties.getOptionalProperty(STATE)
				.flatMap(StatePersistenceStrategy::from)
				.orElse(DEFAULT_STATE_PERSISTENCE_STRATEGY);
		return switch (state) {
			case POSTGRES -> {
				var hibernateProperties = createPostgresProperties(properties);
				var entityManager = HibernateUtil.createEntityManagerFromProperties(hibernateProperties.getProperties());
				yield LdesClientRepositories.sqlBased(entityManager);
			}
			case SQLITE -> {
				var hibernateProperties = createSqliteProperties(properties);
				var entityManager = HibernateUtil.createEntityManagerFromProperties(hibernateProperties.getProperties());
				yield LdesClientRepositories.sqlBased(entityManager);
			}
			case MEMORY -> LdesClientRepositories.memoryBased();
		};
	}

	private static PostgresProperties createPostgresProperties(ComponentProperties properties) {
		String url = properties.getProperty(POSTGRES_URL);
		String username = properties.getProperty(POSTGRES_USERNAME);
		String password = properties.getProperty(POSTGRES_PASSWORD);
		boolean keepState = properties.getOptionalBoolean(KEEP_STATE)
				.orElse(DEFAULT_KEEP_STATE);
		return new PostgresProperties(url, username, password, keepState);
	}

	private static SqliteProperties createSqliteProperties(ComponentProperties properties) {
		final String pipelineName = properties.getPipelineName();
		boolean keepState = properties.getOptionalBoolean(KEEP_STATE)
				.orElse(DEFAULT_KEEP_STATE);
		return properties.getOptionalProperty(SQLITE_DIRECTORY)
				.map(directory -> new SqliteProperties(directory, pipelineName, keepState))
				.orElseGet(() -> new SqliteProperties(pipelineName, keepState));
	}
}
