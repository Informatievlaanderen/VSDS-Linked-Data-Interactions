package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.SqlTreeNodeRepository;

public class TreeNodeRecordRepositoryFactory {
	private TreeNodeRecordRepositoryFactory() {
	}

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
}
