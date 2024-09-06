package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.h2.H2Properties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PersistenceProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatePersistenceFactory {

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	public static final StatePersistenceStrategy DEFAULT_STATE_PERSISTENCE_STRATEGY = StatePersistenceStrategy.MEMORY;
	public static final boolean DEFAULT_KEEP_STATE = false;

	public StatePersistence getStatePersistence(ComponentProperties properties) {
		StatePersistenceStrategy state = properties.getOptionalProperty(PersistenceProperties.STATE)
				.flatMap(StatePersistenceStrategy::from)
				.orElse(DEFAULT_STATE_PERSISTENCE_STRATEGY);
		HibernateProperties hibernateProperties = switch (state) {
			case POSTGRES -> createPostgresProperties(properties);
			case SQLITE -> createSqliteProperties(properties);
			case H2 -> createH2Properties(properties);
			default -> Map::of;
		};
		return StatePersistence.from(state, hibernateProperties, properties.getPipelineName());
	}

	private H2Properties createH2Properties(ComponentProperties properties) {
		return new H2Properties(username, password, url);
	}

	private PostgresProperties createPostgresProperties(ComponentProperties properties) {
		String url = properties.getProperty(PersistenceProperties.POSTGRES_URL);
		String username = properties.getProperty(PersistenceProperties.POSTGRES_USERNAME);
		String password = properties.getProperty(PersistenceProperties.POSTGRES_PASSWORD);
		boolean keepState = properties.getOptionalBoolean(PersistenceProperties.KEEP_STATE)
				.orElse(DEFAULT_KEEP_STATE);
		return new PostgresProperties(url, username, password, keepState);
	}

	private SqliteProperties createSqliteProperties(ComponentProperties properties) {
		final String pipelineName = properties.getPipelineName();
		boolean keepState = properties.getOptionalBoolean(PersistenceProperties.KEEP_STATE)
				.orElse(DEFAULT_KEEP_STATE);
		return properties.getOptionalProperty(PersistenceProperties.SQLITE_DIRECTORY)
				.map(directory -> new SqliteProperties(directory, pipelineName, keepState))
				.orElseGet(() -> new SqliteProperties(pipelineName, keepState));
	}
}
