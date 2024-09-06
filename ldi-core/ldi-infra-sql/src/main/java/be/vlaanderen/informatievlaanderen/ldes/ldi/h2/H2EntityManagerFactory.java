package be.vlaanderen.informatievlaanderen.ldes.ldi.h2;

import be.vlaanderen.informatievlaanderen.ldes.ldi.AbstractEntityManagerFactory;

import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class H2EntityManagerFactory extends AbstractEntityManagerFactory {
	public static final String PERSISTENCE_UNIT_NAME = "pu-h2-jpa";
	private static final Map<String, H2EntityManagerFactory> instances = new HashMap<>();

	private H2EntityManagerFactory(Map<String, String> properties) {
		super(Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties));
	}

	public static synchronized H2EntityManagerFactory getInstance(String instanceName, Map<String, String> properties) {
		return instances.computeIfAbsent(instanceName, s -> new H2EntityManagerFactory(properties));
	}

	@Override
	public void destroyState(String instanceName) {
		super.destroyState(instanceName);
		instances.remove(instanceName);
		// DELETE tables
	}
}
