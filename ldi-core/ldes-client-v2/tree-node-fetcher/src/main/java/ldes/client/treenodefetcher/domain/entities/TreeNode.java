package ldes.client.treenodefetcher.domain.entities;

import java.util.List;

public class TreeNode {
	private final String treeNodeId;
	private final List<String> relation;
	private final List<TreeMember> members;

	public TreeNode(String treeNodeId, List<String> relations, List<TreeMember> members) {
		this.treeNodeId = treeNodeId;
		this.relation = relations;
		this.members = members;
	}

	public String getTreeNodeId() {
		return treeNodeId;
	}

	public List<String> getRelations() {
		return relation;
	}

	public List<TreeMember> getMembers() {
		return members;
	}

}
