package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.apache.nifi.processor.ProcessContext;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties.getStatePersistenceStrategy;

public class StatePersistenceFactory {

	public StatePersistence getStatePersistence(ProcessContext context) {
		StatePersistenceStrategy state = getStatePersistenceStrategy(context);
		HibernateProperties properties = switch (state) {
			case POSTGRES -> createPostgresProperties(context);
			case SQLITE -> createSqliteProperties(context);
			default -> null;
		};
		return StatePersistence.from(state, properties, context.getName());
	}

	private PostgresProperties createPostgresProperties(ProcessContext context) {
		String url = PersistenceProperties.getPostgresUrl(context);
		String username = PersistenceProperties.getPostgresUsername(context);
		String password = PersistenceProperties.getPostgresPassword(context);
		boolean keepState = PersistenceProperties.stateKept(context);
		return new PostgresProperties(url, username, password, keepState);
	}

	private SqliteProperties createSqliteProperties(ProcessContext context) {
		final boolean keepState = PersistenceProperties.stateKept(context);
		return PersistenceProperties.getSqliteDirectory(context)
				.map(directory -> new SqliteProperties(directory, context.getName(), keepState))
				.orElseGet(() -> new SqliteProperties(context.getName(), keepState));
	}
}
