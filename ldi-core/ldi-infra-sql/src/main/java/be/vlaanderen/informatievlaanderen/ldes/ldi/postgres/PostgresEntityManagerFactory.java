package be.vlaanderen.informatievlaanderen.ldes.ldi.postgres;

import be.vlaanderen.informatievlaanderen.ldes.ldi.AbstractEntityManagerFactory;

import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class PostgresEntityManagerFactory extends AbstractEntityManagerFactory {
	public static final String PERSISTENCE_UNIT_NAME = "pu-postgres-jpa";
	private static final Map<String, PostgresEntityManagerFactory> instances = new HashMap<>();


	private PostgresEntityManagerFactory(Map<String, String> properties) {
		super(Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties));
	}

	public static synchronized PostgresEntityManagerFactory getInstance(String instanceName, Map<String, String> properties) {
		return instances.computeIfAbsent(instanceName, s -> new PostgresEntityManagerFactory(properties));
	}


	@Override
	public void destroyState(String instanceName) {
		super.destroyState(instanceName);
		instances.remove(instanceName);
		// DELETE tables
	}
}
