package ldes.client.treenodesupplier.domain.entities;

import ldes.client.treenodefetcher.domain.valueobjects.MutabilityStatus;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TreeNodeRecord {
	private final String treeNodeUrl;
	private TreeNodeStatus treeNodeStatus;
	private LocalDateTime earliestNextVisit;
	private List<String> memberIds;

	public TreeNodeRecord(String treeNodeUrl) {
		this.treeNodeUrl = treeNodeUrl;
		this.treeNodeStatus = TreeNodeStatus.NOT_VISITED;
		this.earliestNextVisit = LocalDateTime.now();
		this.memberIds = new ArrayList<>();
	}

	public TreeNodeRecord(String treeNodeUrl, TreeNodeStatus treeNodeStatus, LocalDateTime earliestNextVisit, List<String> memberIds) {
		this.treeNodeUrl = treeNodeUrl;
		this.treeNodeStatus = treeNodeStatus;
		this.earliestNextVisit = earliestNextVisit;
		this.memberIds = memberIds;
	}

	public String getTreeNodeUrl() {
		return treeNodeUrl;
	}

	public TreeNodeStatus getTreeNodeStatus() {
		return treeNodeStatus;
	}

	public LocalDateTime getEarliestNextVisit() {
		return earliestNextVisit;
	}
	public List<String> getMemberIds() {
		return memberIds;
	}

	public void updateStatus(MutabilityStatus mutabilityStatus) {
		if (mutabilityStatus.isMutable()) {
			treeNodeStatus = TreeNodeStatus.MUTABLE_AND_ACTIVE;
		} else {
			treeNodeStatus = TreeNodeStatus.IMMUTABLE;
		}
		earliestNextVisit = mutabilityStatus.getEarliestNextVisit();
	}

	public boolean hasReceived(String id) {
		return memberIds.contains(id);
	}

	public void addToReceived(List<String> receivedMemberIds) {
		memberIds.addAll(receivedMemberIds);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TreeNodeRecord that))
			return false;
		return Objects.equals(treeNodeUrl, that.treeNodeUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(treeNodeUrl);
	}
}
