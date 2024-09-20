package be.vlaanderen.informatievlaanderen.ldes.ldi.h2;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;

import java.util.Map;

public class H2Properties implements HibernateProperties {
	private final String url;
	private final String username;
	private final String password;

	public H2Properties(String username, String password, String url, String schemaName) {
		this.username = username;
		this.password = password;
		this.url = "%s;MODE=PostgreSQL;INIT=CREATE SCHEMA IF NOT EXISTS \"%s\"\\;SET SCHEMA \"%s\";".formatted(url, schemaName, schemaName);
	}

	public H2Properties(String schemaName) {
		this.username = "sa";
		this.password = "";
		this.url = "%s;MODE=PostgreSQL;INIT=CREATE SCHEMA IF NOT EXISTS \"%s\"\\;SET SCHEMA \"%s\";".formatted(getUrl(), schemaName, schemaName);
	}

	public Map<String, String> getProperties() {
		return Map.of(HIBERNATE_CONNECTION_URL, url,
				HIBERNATE_CONNECTION_USERNAME, username,
				HIBERNATE_CONNECTION_PASSWORD, password,
				HIBERNATE_DIALECT, "org.hibernate.dialect.H2Dialect",
				HIBERNATE_HBM_2_DDL_AUTO, UPDATE);
	}

	private String getUrl() {
		return "jdbc:h2:file:./h2/ldi";
	}
}
