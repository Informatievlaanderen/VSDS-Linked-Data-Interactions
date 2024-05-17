package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.SqlTreeNodeRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite.SqliteEntityManagerFactory;

import java.util.Map;

public class TreeNodeRecordRepositoryFactory {
	private TreeNodeRecordRepositoryFactory() {
	}

	public static TreeNodeRecordRepository getTreeNodeRecordRepository(
			StatePersistenceStrategy statePersistenceStrategy, Map<String, String> properties, String instanceName) {
		return switch (statePersistenceStrategy) {
			case SQLITE -> new SqlTreeNodeRepository(instanceName,
					SqliteEntityManagerFactory.getClientInstance(instanceName, properties));
			case MEMORY -> new InMemoryTreeNodeRecordRepository();
			case POSTGRES -> new SqlTreeNodeRepository(instanceName,
					PostgresEntityManagerFactory.getClientInstance(instanceName, properties));
		};
	}
}
