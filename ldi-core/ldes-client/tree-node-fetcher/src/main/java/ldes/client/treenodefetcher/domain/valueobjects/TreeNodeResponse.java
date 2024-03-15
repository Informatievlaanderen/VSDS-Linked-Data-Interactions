package ldes.client.treenodefetcher.domain.valueobjects;

import ldes.client.treenodefetcher.domain.entities.TreeMember;

import java.util.List;

public class TreeNodeResponse {

	private final String treeNodeUrl;
	private final List<String> relation;
	private final List<TreeMember> members;
	private final MutabilityStatus mutabilityStatus;

	public TreeNodeResponse(String treeNodeUrl, List<String> relations, List<TreeMember> members,
                            MutabilityStatus mutabilityStatus) {
        this.treeNodeUrl = treeNodeUrl;
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

	public String getTreeNodeUrl() {
		return treeNodeUrl;
	}
}
