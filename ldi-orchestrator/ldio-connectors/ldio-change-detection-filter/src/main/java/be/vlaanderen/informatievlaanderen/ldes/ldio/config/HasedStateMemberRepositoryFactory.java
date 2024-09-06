package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.inmemory.InMemoryHashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql.SqlHashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PersistenceProperties;

import java.util.Map;

public class HasedStateMemberRepositoryFactory {
	public static final StatePersistenceStrategy DEFAULT_PERSISTENCE_STRATEGY = StatePersistenceStrategy.MEMORY;
	public static final boolean DEFAULT_KEEP_STATE = false;
	private final ComponentProperties properties;

	public HasedStateMemberRepositoryFactory(ComponentProperties properties) {
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
			case H2 -> null;
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
		final boolean keepState = properties.getOptionalBoolean(PersistenceProperties.KEEP_STATE).orElse(DEFAULT_KEEP_STATE);
		final SqliteProperties sqliteProperties = new SqliteProperties(pipelineName, keepState);
		final var entityManagerFactory = SqliteEntityManagerFactory.getInstance(sqliteProperties);
		return new SqlHashedStateMemberRepository(entityManagerFactory, pipelineName);
	}
}
