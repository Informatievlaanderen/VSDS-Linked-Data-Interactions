package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.SqlTreeNodeRepository;

import javax.persistence.EntityManager;

public class TreeNodeRecordRepositoryFactory {
	private TreeNodeRecordRepositoryFactory() {
	}

	/**
	 * @param statePersistenceStrategy via what persistence strategy the repository should work
	 * @param entityManager a persistence unit to manage the entities
	 * @return the treeNodeRepository for a specific instance (could be a NiFi flow or a LDIO pipeline)
	 */
	public static TreeNodeRecordRepository getTreeNodeRecordRepository(StatePersistenceStrategy statePersistenceStrategy, EntityManager entityManager) {
		return switch (statePersistenceStrategy) {
			case SQLITE, POSTGRES -> new SqlTreeNodeRepository(entityManager);
			case MEMORY -> new InMemoryTreeNodeRecordRepository();
		};
	}
}
