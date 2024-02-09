package ldes.client.treenodesupplier.repository.sql.postgres;

import ldes.client.treenodesupplier.repository.sql.EntityManagerFactory;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

@SuppressWarnings("java:S2696")
public class PostgresEntityManagerFactory implements EntityManagerFactory {

	public static final String PERSISTENCE_UNIT_NAME = "pu-postgres-jpa";
	private static final Map<String, PostgresEntityManagerFactory> instances = new HashMap<>();
	private final EntityManager em;
	private final javax.persistence.EntityManagerFactory emf;

	private PostgresEntityManagerFactory(Map<String, String> properties) {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
		em = emf.createEntityManager();
	}

	public static synchronized PostgresEntityManagerFactory getInstance(String instanceName,
			Map<String, String> properties) {
		return instances.computeIfAbsent(instanceName, s -> new PostgresEntityManagerFactory(properties));
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
