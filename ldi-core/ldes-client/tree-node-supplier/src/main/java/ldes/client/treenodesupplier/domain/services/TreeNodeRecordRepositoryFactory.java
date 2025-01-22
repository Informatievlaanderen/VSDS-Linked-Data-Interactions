package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.SqlTreeNodeRepository;
import ldes.client.treenodesupplier.repository.sql.SqlTreeNodeRepositoryAlt;

import javax.persistence.EntityManager;

public class TreeNodeRecordRepositoryFactory {
	private TreeNodeRecordRepositoryFactory() {
	}

	/**
	 * @param statePersistenceStrategy via what persistence strategy the repository should work
	 * @param properties               a representation of the required config properties to set up the persistence unit
	 * @param instanceName             will be used to be able to more easily keep track of the return repo
	 * @return the treeNodeRecordRepository for a specific instance (could be a NiFi flow or a LDIO pipeline)
	 */
	public static TreeNodeRecordRepository getTreeNodeRecordRepository(
			StatePersistenceStrategy statePersistenceStrategy, HibernateProperties properties, String instanceName) {
		return switch (statePersistenceStrategy) {
			case SQLITE -> new SqlTreeNodeRepository(instanceName,
					SqliteEntityManagerFactory.getInstance(properties));
			case MEMORY -> new InMemoryTreeNodeRecordRepository();
			case POSTGRES -> new SqlTreeNodeRepository(instanceName,
					PostgresEntityManagerFactory.getInstance(instanceName, properties.getProperties()));
		};
	}

	public static TreeNodeRecordRepository getTreeNodeRecordRepository(StatePersistenceStrategy statePersistenceStrategy, EntityManager entityManager) {
		return switch (statePersistenceStrategy) {
			case SQLITE, POSTGRES -> new SqlTreeNodeRepositoryAlt(entityManager);
			case MEMORY -> new InMemoryTreeNodeRecordRepository();
		};
	}
}
