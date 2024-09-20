package be.vlaanderen.informatievlaanderen.ldes.ldi;

import javax.persistence.EntityManager;

/**
 * Custom interface to manage the EntityManager, EntityManagerFactory from the javax library and their lifecycle more easily
 */
public interface EntityManagerFactory {
	/**
	 * @return the enityManager that is managed by the javax library
	 */
	EntityManager getEntityManager();

	int executeStatelessQuery(StatelessQueryExecutor queryExecutor);

	/**
	 * Destroy the EntityManager and EntityManagerFactory from the javax library for the specified instanceName
	 *
	 * @param instanceName name of the instances from which the EntityManager and EntityManagerFactory must be destroyed
	 */
	void destroyState(String instanceName);
}
