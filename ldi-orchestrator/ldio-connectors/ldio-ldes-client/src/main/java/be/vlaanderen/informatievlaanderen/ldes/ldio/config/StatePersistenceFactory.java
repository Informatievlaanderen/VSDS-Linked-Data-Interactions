package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.sql.postgres.PostgresProperties;

import java.util.Map;

public class StatePersistenceFactory {

	public static final StatePersistenceStrategy DEFAULT_STATE_PERSISTENCE_STRATEGY = StatePersistenceStrategy.MEMORY;
	public static final boolean DEFAULT_KEEP_STATE = false;

	public StatePersistence getStatePersistence(ComponentProperties properties) {
		StatePersistenceStrategy state = properties.getOptionalProperty(LdioLdesClientProperties.STATE)
				.flatMap(StatePersistenceStrategy::from)
				.orElse(DEFAULT_STATE_PERSISTENCE_STRATEGY);
		Map<String, String> persistenceProperties = Map.of();
		if (state.equals(StatePersistenceStrategy.POSTGRES)) {
			persistenceProperties = createPostgresProperties(properties);
		}
		return StatePersistence.from(state, persistenceProperties);
	}

	private Map<String, String> createPostgresProperties(ComponentProperties properties) {
		String url = properties.getProperty(LdioLdesClientProperties.POSTGRES_URL);
		String username = properties.getProperty(LdioLdesClientProperties.POSTGRES_USERNAME);
		String password = properties.getProperty(LdioLdesClientProperties.POSTGRES_PASSWORD);
		boolean keepState = properties.getOptionalBoolean(LdioLdesClientProperties.KEEP_STATE)
				.orElse(DEFAULT_KEEP_STATE);
		return new PostgresProperties(url, username, password, keepState).getProperties();
	}
}
