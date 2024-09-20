package be.vlaanderen.informatievlaanderen.ldes.ldi.h2;


import be.vlaanderen.informatievlaanderen.ldes.ldi.AbstractEntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;


public class H2EntityManager extends AbstractEntityManagerFactory {
	public static final String PERSISTENCE_UNIT_NAME = "pu-h2-jpa";
	private static final Map<String, H2EntityManager> instances = new HashMap<>();

	private H2EntityManager(Map<String, String> properties) {
		super(Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties).createEntityManager());
	}

	public static synchronized H2EntityManager getInstance(String instanceName, Map<String, String> properties) {
		return instances.computeIfAbsent(instanceName, s -> new H2EntityManager(properties));
	}

	@Override
	public void destroyState(String instanceName) {
		var instance = instances.get(instanceName);
		if (instance != null) {
			var em = instance.getEntityManager();
			em.getTransaction().begin();
			em.createNativeQuery("DROP SCHEMA \"%s\" CASCADE;".formatted(instanceName)).executeUpdate();
			em.getTransaction().commit();
			super.destroyState(instanceName);
			instances.remove(instanceName);
		}

	}
}
