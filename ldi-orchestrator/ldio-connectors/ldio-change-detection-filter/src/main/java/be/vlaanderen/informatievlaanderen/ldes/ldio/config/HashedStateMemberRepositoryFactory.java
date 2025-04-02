package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateUtil;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.inmemory.InMemoryHashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql.SqlHashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PersistenceProperties;

public class HashedStateMemberRepositoryFactory {
	public static final StatePersistenceStrategy DEFAULT_PERSISTENCE_STRATEGY = StatePersistenceStrategy.MEMORY;
	public static final boolean DEFAULT_KEEP_STATE = false;

	private HashedStateMemberRepositoryFactory() {
	}

	public static HashedStateMemberRepository getHashedStateMemberRepository(ComponentProperties properties) {
		final StatePersistenceStrategy persistenceStrategy = properties.getOptionalProperty(PersistenceProperties.STATE)
				.flatMap(StatePersistenceStrategy::from)
				.orElse(DEFAULT_PERSISTENCE_STRATEGY);

		return switch (persistenceStrategy) {
			case POSTGRES -> {
				var hibernateProperties = createPostgresProperties(properties);
				var entityManager = HibernateUtil.createEntityManagerFromProperties(hibernateProperties.getProperties());
				yield new SqlHashedStateMemberRepository(entityManager);
			}
			case SQLITE -> {
				var hibernateProperties = createSqliteProperties(properties);
				var entityManager = HibernateUtil.createEntityManagerFromProperties(hibernateProperties.getProperties());
				yield new SqlHashedStateMemberRepository(entityManager);
			}
			case MEMORY -> new InMemoryHashedStateMemberRepository();
		};
	}

	private static PostgresProperties createPostgresProperties(ComponentProperties properties) {
		String url = properties.getProperty(PersistenceProperties.POSTGRES_URL);
		String username = properties.getProperty(PersistenceProperties.POSTGRES_USERNAME);
		String password = properties.getProperty(PersistenceProperties.POSTGRES_PASSWORD);
		boolean keepState = properties.getOptionalBoolean(PersistenceProperties.KEEP_STATE)
				.orElse(DEFAULT_KEEP_STATE);
		return new PostgresProperties(url, username, password, keepState);
	}

	private static SqliteProperties createSqliteProperties(ComponentProperties properties) {
		final String pipelineName = properties.getPipelineName();
		final boolean keepState = properties.getOptionalBoolean(PersistenceProperties.KEEP_STATE).orElse(DEFAULT_KEEP_STATE);
		return properties.getOptionalProperty(PersistenceProperties.SQLITE_DIRECTORY)
				.map(directory -> new SqliteProperties(directory, pipelineName, keepState))
				.orElse(new SqliteProperties(pipelineName, keepState));
	}
}
