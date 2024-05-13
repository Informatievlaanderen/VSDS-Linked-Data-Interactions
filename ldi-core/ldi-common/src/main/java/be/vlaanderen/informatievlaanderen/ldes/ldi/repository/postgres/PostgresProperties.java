package be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres;

import java.util.Map;

public class PostgresProperties {
	public static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";
	public static final String HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";
	public static final String HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";
	public static final String HIBERNATE_HBM_2_DDL_AUTO = "hibernate.hbm2ddl.auto";
	public static final String UPDATE = "update";
	public static final String CREATE_DROP = "create-drop";
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
				HIBERNATE_HBM_2_DDL_AUTO, keepState ? UPDATE : CREATE_DROP);
	}

}
