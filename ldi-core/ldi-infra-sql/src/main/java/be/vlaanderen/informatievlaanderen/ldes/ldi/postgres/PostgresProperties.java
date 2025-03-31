package be.vlaanderen.informatievlaanderen.ldes.ldi.postgres;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;

import java.util.Map;

public class PostgresProperties implements HibernateProperties {
	public static final String DIALECT = "org.hibernate.dialect.PostgreSQLDialect";
	public static final String DRIVER = "org.postgresql.Driver";
	private final String url;
	private final String username;
	private final String password;
	private final boolean keepState;

	public PostgresProperties(String url, String username, String password, boolean keepState) {
		this.url = url;
		this.username = username;
		this.password = password;
		this.keepState = keepState;
	}

	public Map<String, String> getProperties() {
		return Map.of(HIBERNATE_CONNECTION_URL, url,
				HIBERNATE_CONNECTION_USERNAME, username,
				HIBERNATE_CONNECTION_PASSWORD, password,
				HIBERNATE_DIALECT, DIALECT,
				HIBERNATE_DRIVER_CLASS, DRIVER,
				HIBERNATE_HBM_2_DDL_AUTO, keepState ? UPDATE : CREATE_DROP);
	}

}
