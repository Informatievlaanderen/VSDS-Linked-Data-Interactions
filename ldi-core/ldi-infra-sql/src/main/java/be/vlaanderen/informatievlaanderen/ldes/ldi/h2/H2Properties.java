package be.vlaanderen.informatievlaanderen.ldes.ldi.h2;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;

import java.util.Map;

public class H2Properties implements HibernateProperties {
	private final String url;
	private final String username;
	private final String password;

	public H2Properties(String username, String password, String url) {
		this.username = username;
		this.password = password;
		this.url = url;
	}

	public H2Properties(boolean keepState) {
		this.username = "sa";
		this.password = "";
		this.url = getUrl(keepState);
	}

	public Map<String, String> getProperties() {
		return Map.of(HIBERNATE_CONNECTION_URL, url,
				HIBERNATE_CONNECTION_USERNAME, username,
				HIBERNATE_CONNECTION_PASSWORD, password,
				HIBERNATE_DIALECT, "org.hibernate.dialect.H2Dialect",
				HIBERNATE_HBM_2_DDL_AUTO, UPDATE);
	}

	private String getUrl(boolean keepState) {
		return keepState ? "jdbc:h2:mem:testdb" : "jdbc:h2:file:/path/to/your/database";
	}
}
