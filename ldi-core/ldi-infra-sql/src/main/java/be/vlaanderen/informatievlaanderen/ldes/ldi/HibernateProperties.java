package be.vlaanderen.informatievlaanderen.ldes.ldi;

import java.util.Map;

@FunctionalInterface
public interface HibernateProperties {
	String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";
	String HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";
	String HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";
	String HIBERNATE_HBM_2_DDL_AUTO = "hibernate.hbm2ddl.auto";
	String UPDATE = "update";
	String CREATE_DROP = "create-drop";

	Map<String, String> getProperties();
}
