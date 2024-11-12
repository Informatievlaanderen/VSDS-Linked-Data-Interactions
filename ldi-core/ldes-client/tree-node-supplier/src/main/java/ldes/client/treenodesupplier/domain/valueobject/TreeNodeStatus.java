package ldes.client.treenodesupplier.domain.valueobject;

/**
 * Representation how much of a TreeNode has been processed
 */
public enum TreeNodeStatus {
	NOT_VISITED,
	MUTABLE_AND_ACTIVE,
	IMMUTABLE_WITH_UNPROCESSED_MEMBERS,
	IMMUTABLE_WITHOUT_UNPROCESSED_MEMBERS
}
