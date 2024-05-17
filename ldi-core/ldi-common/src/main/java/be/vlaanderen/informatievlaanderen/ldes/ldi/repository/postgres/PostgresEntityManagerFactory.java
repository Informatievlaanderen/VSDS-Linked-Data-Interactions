package be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres;

import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.EntityManagerFactory;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class PostgresEntityManagerFactory implements EntityManagerFactory {

	public static final String PERSISTENCE_UNIT_NAME_CLIENT = "pu-postgres-jpa-client";
	public static final String PERSISTENCE_UNIT_POSTGRES_CHANGE_DETECTION_FILTER = "pu-postgres-jpa-change-detection-filter";
	private static final Map<String, PostgresEntityManagerFactory> instances = new HashMap<>();
	private final EntityManager em;
	private final javax.persistence.EntityManagerFactory emf;

	private PostgresEntityManagerFactory(String persistenceUnitName, Map<String, String> properties) {
		emf = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
		em = emf.createEntityManager();
	}

	public static synchronized PostgresEntityManagerFactory getClientInstance(String instanceName, Map<String, String> properties) {
		return getInstance(PERSISTENCE_UNIT_NAME_CLIENT, instanceName, properties);
	}

	public static synchronized PostgresEntityManagerFactory getInstance(String persistenceUnitName, String instanceName, Map<String, String> properties) {
		return instances.computeIfAbsent(instanceName, s -> new PostgresEntityManagerFactory(persistenceUnitName, properties));
	}


	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public void destroyState(String instanceName) {
		em.close();
		emf.close();
		instances.remove(instanceName);
		// DELETE tables
	}
}
