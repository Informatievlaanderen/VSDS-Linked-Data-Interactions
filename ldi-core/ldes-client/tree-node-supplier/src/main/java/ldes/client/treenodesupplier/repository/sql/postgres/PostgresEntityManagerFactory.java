package ldes.client.treenodesupplier.repository.sql.postgres;

import ldes.client.treenodesupplier.repository.sql.EntityManagerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

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

	public static synchronized PostgresEntityManagerFactory getInstance(Map<String, String> properties,
			String instanceName) {
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

	public void destroyState() {
		em.close();
		emf.close();
		instances.clear();
		// DELETE tables
	}
}
