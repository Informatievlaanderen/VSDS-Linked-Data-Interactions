package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.inmemory.InMemoryHashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql.SqlHashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite.SqliteEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects.StatePersistenceStrategy;
import org.apache.nifi.processor.ProcessContext;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties.getStatePersistenceStrategy;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite.SqliteEntityManagerFactory.DATABASE_DIRECTORY_KEY;

public class HashedMemberRepositoryFactory {
	private HashedMemberRepositoryFactory() {}

	public static HashedStateMemberRepository getRepository(ProcessContext context) {
		StatePersistenceStrategy state = getStatePersistenceStrategy(context);
		return switch (state) {
			case POSTGRES -> createPostgresRepository(context);
			case SQLITE -> createSqliteRepository(context);
			case MEMORY -> new InMemoryHashedStateMemberRepository();
		};
	}

	private static SqlHashedStateMemberRepository createPostgresRepository(ProcessContext context) {
		final String instanceName = context.getName();
		final var entityManagerFactory = PostgresEntityManagerFactory.getInstance(instanceName, createPostgresProperties(context));
		return new SqlHashedStateMemberRepository(entityManagerFactory, instanceName);
	}

	private static Map<String, String> createPostgresProperties(ProcessContext context) {
		String url = PersistenceProperties.getPostgresUrl(context);
		String username = PersistenceProperties.getPostgresUsername(context);
		String password = PersistenceProperties.getPostgresPassword(context);
		boolean keepState = PersistenceProperties.stateKept(context);
		return new PostgresProperties(url, username, password, keepState).getProperties();
	}

	private static SqlHashedStateMemberRepository createSqliteRepository(ProcessContext context) {
		final String instanceName = context.getName();
		final Map<String, String> sqliteProperties = Map.of(
				DATABASE_DIRECTORY_KEY,
				PersistenceProperties.getSqliteDirectory(context).orElse("change-detection-filter"));
		final var entityManagerFactory = SqliteEntityManagerFactory.getInstance(instanceName, sqliteProperties);
		return new SqlHashedStateMemberRepository(entityManagerFactory, instanceName);
	}
}