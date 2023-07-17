package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.sql.postgres.PostgresProperties;
import org.apache.nifi.processor.ProcessContext;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties.getStatePersistenceStrategy;

public class StatePersistenceFactory {

	public StatePersistence getStatePersistence(ProcessContext context) {
		StatePersistenceStrategy state = getStatePersistenceStrategy(context);
		Map<String, String> persistenceProperties = Map.of();
		if (state.equals(StatePersistenceStrategy.POSTGRES)) {
			persistenceProperties = createPostgresProperties(context);
		}
		return StatePersistence.from(state, persistenceProperties);
	}

	private Map<String, String> createPostgresProperties(ProcessContext context) {
		String url = LdesProcessorProperties.getPostgresUrl(context);
		String username = LdesProcessorProperties.getPostgresUsername(context);
		String password = LdesProcessorProperties.getPostgresPassword(context);
		boolean keepState = LdesProcessorProperties.stateKept(context);
		return new PostgresProperties(url, username, password, keepState).getProperties();
	}
}
