package be.vlaanderen.informatievlaanderen.ldes.ldi.postgres;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;

import java.util.Map;

public class PostgresProperties implements HibernateProperties {
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
