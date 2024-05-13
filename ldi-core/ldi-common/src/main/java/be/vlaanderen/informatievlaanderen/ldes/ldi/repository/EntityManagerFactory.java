package be.vlaanderen.informatievlaanderen.ldes.ldi.repository;

import javax.persistence.EntityManager;

public interface EntityManagerFactory {
	EntityManager getEntityManager();

	void destroyState(String instanceName);
}
