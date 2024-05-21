package ldes.client.treenodesupplier.repository.mapper;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.TreeNodeRecordEntity;
import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

public class TreeNodeRecordEntityMapper {
	private TreeNodeRecordEntityMapper() {
	}

	public static TreeNodeRecordEntity fromTreeNodeRecord(TreeNodeRecord treeMember) {
		return new TreeNodeRecordEntity(treeMember.getTreeNodeUrl(), treeMember.getTreeNodeStatus().name(),
				treeMember.getEarliestNextVisit(), treeMember.getMemberIds());
	}

	public static TreeNodeRecord toTreeNode(TreeNodeRecordEntity treeNodeRecordEntity) {
		TreeNodeStatus treeNodeStatus = TreeNodeStatus.valueOf(treeNodeRecordEntity.getTreeNodeStatus());
		return new TreeNodeRecord(treeNodeRecordEntity.getTreeNodeUrl(), treeNodeStatus, treeNodeRecordEntity.getEarliestNextVisit(), treeNodeRecordEntity.getMembers());
	}
}
