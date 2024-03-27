package ldes.client.treenodesupplier.repository;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

import java.util.Optional;

public interface TreeNodeRecordRepository {
	void saveTreeNodeRecord(TreeNodeRecord processedTreenode);

	boolean existsById(String treeNodeId);

	Optional<TreeNodeRecord> getTreeNodeRecordWithStatusAndEarliestNextVisit(TreeNodeStatus treeNodeStatus);

	boolean existsByIdAndStatus(String treeNodeId, TreeNodeStatus treeNodeStatus);

	void destroyState();

	boolean containsTreeNodeRecords();

	void resetContext();
}
