package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.filebased.FileBasedTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.SqlTreeNodeRepository;
import ldes.client.treenodesupplier.repository.sql.postgres.PostgresEntityManagerFactory;
import ldes.client.treenodesupplier.repository.sql.sqlite.SqliteEntityManagerFactory;

import java.util.Map;

public class TreeNodeRecordRepositoryFactory {
	private TreeNodeRecordRepositoryFactory() {
	}

	public static TreeNodeRecordRepository getTreeNodeRecordRepository(
			StatePersistenceStrategy statePersistenceStrategy, Map<String, String> properties) {
		return switch (statePersistenceStrategy) {
			case SQLITE -> new SqlTreeNodeRepository(SqliteEntityManagerFactory.getInstance());
			case MEMORY -> new InMemoryTreeNodeRecordRepository();
			case FILE -> new FileBasedTreeNodeRecordRepository();
			case POSTGRES -> new SqlTreeNodeRepository(PostgresEntityManagerFactory.getInstance(properties));
		};
	}
}
