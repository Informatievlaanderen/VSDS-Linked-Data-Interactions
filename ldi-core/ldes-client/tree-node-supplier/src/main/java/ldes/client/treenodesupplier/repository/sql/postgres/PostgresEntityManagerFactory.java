package ldes.client.treenodesupplier.repository.sql.postgres;

import ldes.client.treenodesupplier.repository.sql.EntityManagerFactory;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

@SuppressWarnings("java:S2696")
public class PostgresEntityManagerFactory implements EntityManagerFactory {

	public static final String PERSISTENCE_UNIT_NAME = "pu-postgres-jpa";
	private static PostgresEntityManagerFactory instance = null;
	private final EntityManager em;
	private final javax.persistence.EntityManagerFactory emf;

	private PostgresEntityManagerFactory(Map<String, String> properties) {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
		em = emf.createEntityManager();
	}

	public static synchronized PostgresEntityManagerFactory getInstance(Map<String, String> properties) {
		if (instance == null) {
			instance = new PostgresEntityManagerFactory(properties);
		}

		return instance;
	}

	public EntityManager getEntityManager() {
		return em;
	}

	public void destroyState() {
		em.close();
		emf.close();
		instance = null;
		// DELETE tables
	}
}
