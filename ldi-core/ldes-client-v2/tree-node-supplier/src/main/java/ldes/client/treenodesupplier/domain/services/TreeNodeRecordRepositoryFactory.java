package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.domain.valueobject.StatePersistanceStrategy;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sqlite.SqliteTreeNodeRepository;

public class TreeNodeRecordRepositoryFactory {

	public static TreeNodeRecordRepository getMemberRepository(StatePersistanceStrategy statePersistanceStrategy) {
		return switch (statePersistanceStrategy) {
			case SQLITE -> new SqliteTreeNodeRepository();
			case MEMORY -> new InMemoryTreeNodeRecordRepository();
		};
	}
}
