package be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;

import java.util.Map;

public class SqliteProperties implements HibernateProperties {
	public static final String DATABASE_DIRECTORY = "sqlite";

	private final String databaseDirectory;
	private final String instanceName;
	private final boolean keepState;

	public SqliteProperties(String databaseDirectory, String instanceName, boolean keepState) {
		this.databaseDirectory = databaseDirectory;
		this.instanceName = instanceName;
		this.keepState = keepState;
	}

	public SqliteProperties(String instanceName, boolean keepState) {
		this(DATABASE_DIRECTORY, instanceName, keepState);
	}

	public String getInstanceName() {
		return instanceName;
	}

	public String getDatabaseDirectory() {
		return databaseDirectory;
	}

	public String getDatabaseName() {
		return instanceName + ".db";
	}

	@Override
	public Map<String, String> getProperties() {
		return Map.of("javax.persistence.jdbc.url",
				"jdbc:sqlite:./%s/%s".formatted(databaseDirectory, getDatabaseName()),
				HIBERNATE_DIALECT, "org.sqlite.hibernate.dialect.SQLiteDialect",
				"javax.persistence.jdbc.driver", "org.sqlite.JDBC",
				"hibernate.connection.provider_class", "com.zaxxer.hikari.hibernate.HikariConnectionProvider",
				HIBERNATE_HBM_2_DDL_AUTO, keepState ? UPDATE : CREATE_DROP);
	}
}
