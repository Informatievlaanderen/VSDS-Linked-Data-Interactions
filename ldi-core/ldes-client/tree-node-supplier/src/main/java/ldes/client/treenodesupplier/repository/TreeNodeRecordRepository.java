package ldes.client.treenodesupplier.repository;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

import java.util.Optional;

public interface TreeNodeRecordRepository {
	/**
	 * Saves a processed TreeNodeRecord to the repository
	 */
	void saveTreeNodeRecord(TreeNodeRecord processedTreeNode);

	/**
	 * Checks whether a tree node with the specified id exists
	 *
	 * @return <code>true</code> if the tree node exists
	 */
	boolean existsById(String treeNodeId);

	/**
	 * Searches the first TreeNodeRecord with the specified TreeNodeStatus and has the earliest
	 * {@link TreeNodeRecord#getEarliestNextVisit()} value
	 *
	 * @param treeNodeStatus the status that the desired TreeNodeRecord must have
	 * @return A TreeNodeRecord with the specified status, or an empty optional
	 */
	Optional<TreeNodeRecord> getTreeNodeRecordWithStatusAndEarliestNextVisit(TreeNodeStatus treeNodeStatus);

	/**
	 * Checks whether a tree node with the specified id and TreeNodeStatus exists
	 *
	 * @return <code>true</code> if the tree node exists
	 */
	boolean existsByIdAndStatus(String treeNodeId, TreeNodeStatus treeNodeStatus);

	/**
	 * Clean up the repository when it is not used anymore
	 */
	void destroyState();

	/**
	 * Checks whether any processable TreeNodeRecords are present in the repository
	 *
	 * @return a boolean whether
	 */
	boolean containsTreeNodeRecords();

	/**
	 * Makes sure the context is clear again
	 */
	void resetContext();
}
