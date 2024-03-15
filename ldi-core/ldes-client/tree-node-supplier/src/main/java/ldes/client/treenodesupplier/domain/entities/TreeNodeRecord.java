package ldes.client.treenodesupplier.domain.entities;

import ldes.client.treenodefetcher.domain.valueobjects.MutabilityStatus;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class TreeNodeRecord {
	private final String treeNodeUrl;
	private TreeNodeStatus treeNodeStatus;
	private LocalDateTime earliestNextVisit;

	public TreeNodeRecord(String treeNodeUrl) {
		this.treeNodeUrl = treeNodeUrl;
		this.treeNodeStatus = TreeNodeStatus.NOT_VISITED;
		this.earliestNextVisit = LocalDateTime.now();
	}

	public TreeNodeRecord(String treeNodeUrl, TreeNodeStatus treeNodeStatus, LocalDateTime earliestNextVisit) {
		this.treeNodeUrl = treeNodeUrl;
		this.treeNodeStatus = treeNodeStatus;
		this.earliestNextVisit = earliestNextVisit;
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

	public void updateStatus(MutabilityStatus mutabilityStatus) {
		if (mutabilityStatus.isMutable()) {
			treeNodeStatus = TreeNodeStatus.MUTABLE_AND_ACTIVE;
		} else {
			treeNodeStatus = TreeNodeStatus.IMMUTABLE_WITH_UNPROCESSED_MEMBERS;
		}
		earliestNextVisit = mutabilityStatus.getEarliestNextVisit();
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

	public void markImmutableWithoutUnprocessedMembers() {
		treeNodeStatus = TreeNodeStatus.IMMUTABLE_WITHOUT_UNPROCESSED_MEMBERS;
	}
}
