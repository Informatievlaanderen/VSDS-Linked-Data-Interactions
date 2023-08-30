package ldes.client.treenodesupplier.repository.sql;

import javax.persistence.EntityManager;

public interface EntityManagerFactory {
	EntityManager getEntityManager();
	default void destroyState(String instanceName) {};
}
