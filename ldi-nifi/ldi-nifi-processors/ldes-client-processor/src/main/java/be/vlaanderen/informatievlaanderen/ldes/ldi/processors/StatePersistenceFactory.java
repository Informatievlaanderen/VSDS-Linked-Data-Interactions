package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.apache.nifi.processor.ProcessContext;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties.getStatePersistenceStrategy;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite.SqliteEntityManagerFactory.DATABASE_DIRECTORY_KEY;

public class StatePersistenceFactory {

	public StatePersistence getStatePersistence(ProcessContext context) {
		StatePersistenceStrategy state = getStatePersistenceStrategy(context);
		Map<String, String> persistenceProperties = switch (state) {
			case POSTGRES -> createPostgresProperties(context);
			case SQLITE -> Map.of(DATABASE_DIRECTORY_KEY, PersistenceProperties.getSqliteDirectory(context).orElse("ldes-client"));
			default -> Map.of();
		};
		return StatePersistence.from(state, persistenceProperties, context.getName());
	}

	private Map<String, String> createPostgresProperties(ProcessContext context) {
		String url = PersistenceProperties.getPostgresUrl(context);
		String username = PersistenceProperties.getPostgresUsername(context);
		String password = PersistenceProperties.getPostgresPassword(context);
		boolean keepState = PersistenceProperties.stateKept(context);
		return new PostgresProperties(url, username, password, keepState).getProperties();
	}
}
