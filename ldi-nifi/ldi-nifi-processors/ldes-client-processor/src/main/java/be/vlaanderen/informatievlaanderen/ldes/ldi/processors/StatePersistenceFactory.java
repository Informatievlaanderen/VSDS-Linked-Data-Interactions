package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects.StatePersistenceStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres.PostgresProperties;
import org.apache.nifi.processor.ProcessContext;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties.getStatePersistenceStrategy;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite.SqliteEntityManagerFactory.DATABASE_DIRECTORY_KEY;

public class StatePersistenceFactory {

	public StatePersistence getStatePersistence(ProcessContext context) {
		StatePersistenceStrategy state = getStatePersistenceStrategy(context);
		Map<String, String> persistenceProperties = switch (state) {
			case POSTGRES -> createPostgresProperties(context);
			case SQLITE -> Map.of(DATABASE_DIRECTORY_KEY, LdesProcessorProperties.getSqliteDirectory(context));
			default -> Map.of();
		};
		return StatePersistence.from(state, persistenceProperties, context.getName());
	}

	private Map<String, String> createPostgresProperties(ProcessContext context) {
		String url = LdesProcessorProperties.getPostgresUrl(context);
		String username = LdesProcessorProperties.getPostgresUsername(context);
		String password = LdesProcessorProperties.getPostgresPassword(context);
		boolean keepState = LdesProcessorProperties.stateKept(context);
		return new PostgresProperties(url, username, password, keepState).getProperties();
	}
}
