package ldes.client.treenodefetcher.domain.entities;

import ldes.client.treenodefetcher.domain.valueobjects.MutabilityStatus;

import java.util.List;

public class TreeNode {
	private final String treeNodeId;
	private final List<String> relation;
	private final List<TreeMember> members;

	private final MutabilityStatus mutabilityStatus;

	public TreeNode(String treeNodeId, List<String> relations, List<TreeMember> members,
			MutabilityStatus mutabilityStatus) {
		this.treeNodeId = treeNodeId;
		this.relation = relations;
		this.members = members;
		this.mutabilityStatus = mutabilityStatus;
	}

	public List<String> getRelations() {
		return relation;
	}

	public List<TreeMember> getMembers() {
		return members;
	}

	public MutabilityStatus getMutabilityStatus() {
		return mutabilityStatus;
	}
}
