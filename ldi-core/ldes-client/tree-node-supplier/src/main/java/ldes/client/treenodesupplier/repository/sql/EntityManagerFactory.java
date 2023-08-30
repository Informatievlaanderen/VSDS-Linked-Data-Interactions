package ldes.client.treenodesupplier.repository.sql;

import javax.persistence.EntityManager;

public interface EntityManagerFactory {
	EntityManager getEntityManager();

	void destroyState();
	default void destroyState(String instanceName) {};
}
