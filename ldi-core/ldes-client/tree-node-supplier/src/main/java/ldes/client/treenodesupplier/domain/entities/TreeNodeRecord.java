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
	private final List<String> memberIds;

	public TreeNodeRecord(String treeNodeUrl) {
		this(treeNodeUrl, TreeNodeStatus.NOT_VISITED, LocalDateTime.now(), new ArrayList<>());
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

	/**
	 * @return a representation of how much of this TreeNode has been processed
	 */
	public TreeNodeStatus getTreeNodeStatus() {
		return treeNodeStatus;
	}

	/**
	 * Keeps track when this TreeNode could be changed at the earliest, fetching this TreeNode again before this
	 * timestamp is pointless
	 *
	 * @return a timestamp suggesting when to fetch this TreeNode again
	 */
	public LocalDateTime getEarliestNextVisit() {
		return earliestNextVisit;
	}

	/**
	 * @return a list of all the id of all the members that are part of this TreeNode
	 */
	public List<String> getMemberIds() {
		return memberIds;
	}

	/**
	 * Updates the TreeNodeStatus based on the mutabilityStatus received from the HTTP response
	 */
	public void updateStatus(MutabilityStatus mutabilityStatus) {
		if (mutabilityStatus.isMutable()) {
			treeNodeStatus = TreeNodeStatus.MUTABLE_AND_ACTIVE;
		} else {
			treeNodeStatus = TreeNodeStatus.IMMUTABLE_WITH_UNPROCESSED_MEMBERS;
		}
		earliestNextVisit = mutabilityStatus.getEarliestNextVisit();
	}

	/**
	 * Check whether the member has already been received
	 *
	 * @param memberId the id of the member that needs to be checked
	 * @return <code>true</code> if the member is new, <code>false</code> if the member has already been received before
	 */
	public boolean hasReceived(String memberId) {
		return memberIds.contains(memberId);
	}

	/**
	 * Add newly received members to this TreeNode
	 *
	 * @param receivedMemberIds the ids of the new members
	 */
	public void addToReceived(List<String> receivedMemberIds) {
		memberIds.addAll(receivedMemberIds);
	}

	/**
	 * Marks this TreeNode as completely processed.
	 * <br />
	 * To save some resources, the list of members is cleared, as it does not matter anymore which members of
	 * this TreeNode have been processed
	 */
	public void markImmutableWithoutUnprocessedMembers() {
		memberIds.clear();
		treeNodeStatus = TreeNodeStatus.IMMUTABLE_WITHOUT_UNPROCESSED_MEMBERS;
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
