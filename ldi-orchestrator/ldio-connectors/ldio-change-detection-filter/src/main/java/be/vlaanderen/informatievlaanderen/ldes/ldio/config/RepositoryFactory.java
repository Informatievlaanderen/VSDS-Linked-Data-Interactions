package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.inmemory.InMemoryHashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql.SqlHashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite.SqliteEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects.StatePersistenceStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.persistence.PersistenceProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite.SqliteEntityManagerFactory.DATABASE_DIRECTORY_KEY;

public class RepositoryFactory {
	public static final StatePersistenceStrategy DEFAULT_PERSISTENCE_STRATEGY = StatePersistenceStrategy.MEMORY;
	public static final boolean DEFAULT_KEEP_STATE = false;
	private final ComponentProperties properties;

	public RepositoryFactory(ComponentProperties properties) {
		this.properties = properties;
	}

	public HashedStateMemberRepository getHashedStateMemberRepository() {
		final StatePersistenceStrategy persistenceStrategy = properties.getOptionalProperty(PersistenceProperties.STATE)
				.flatMap(StatePersistenceStrategy::from)
				.orElse(DEFAULT_PERSISTENCE_STRATEGY);
		return switch (persistenceStrategy) {
			case POSTGRES -> createPostgresRepository();
			case SQLITE -> createSqliteRepository();
			case MEMORY -> new InMemoryHashedStateMemberRepository();
		};
	}

	private SqlHashedStateMemberRepository createPostgresRepository() {
		final String pipelineName = properties.getPipelineName();
		final var entityManagerFactory = PostgresEntityManagerFactory.getInstance(pipelineName,
				createPostgresProperties());
		return new SqlHashedStateMemberRepository(entityManagerFactory, pipelineName);
	}

	private Map<String, String> createPostgresProperties() {
		String url = properties.getProperty(PersistenceProperties.POSTGRES_URL);
		String username = properties.getProperty(PersistenceProperties.POSTGRES_USERNAME);
		String password = properties.getProperty(PersistenceProperties.POSTGRES_PASSWORD);
		boolean keepState = properties.getOptionalBoolean(PersistenceProperties.KEEP_STATE)
				.orElse(DEFAULT_KEEP_STATE);
		return new PostgresProperties(url, username, password, keepState).getProperties();
	}

	private SqlHashedStateMemberRepository createSqliteRepository() {
		final String pipelineName = properties.getPipelineName();
		final Map<String, String> sqliteProperties = Map.of(DATABASE_DIRECTORY_KEY, properties.getOptionalProperty(PersistenceProperties.SQLITE_DIRECTORY).orElse("change-detection-filter"));
		final var entityManagerFactory = SqliteEntityManagerFactory.getInstance(pipelineName, sqliteProperties);
		return new SqlHashedStateMemberRepository(entityManagerFactory, pipelineName);
	}
}
