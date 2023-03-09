package ldes.client.treenodesupplier.repository.sqlite;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "TreeNode.countById", query = "SELECT COUNT(t) FROM TreeNodeRecordEntity t WHERE t.id = :id")
@NamedQuery(name = "TreeNode.countByIdAndStatus", query = "SELECT COUNT(t) FROM TreeNodeRecordEntity t WHERE t.id = :id and t.treeNodeStatus = :treeNodeStatus")
@NamedQuery(name = "TreeNode.getByTreeNodeStatus", query = "SELECT t FROM TreeNodeRecordEntity t WHERE t.treeNodeStatus = :treeNodeStatus")
public class TreeNodeRecordEntity {

	@Id
	private String treeNodeUrl;
	private TreeNodeStatus treeNodeStatus;
	private LocalDateTime earliestNextVisit;

	public TreeNodeRecordEntity() {
	}

	public TreeNodeRecordEntity(String treeNodeUrl, TreeNodeStatus treeNodeStatus, LocalDateTime earliestNextVisit) {
		this.treeNodeUrl = treeNodeUrl;
		this.treeNodeStatus = treeNodeStatus;
		this.earliestNextVisit = earliestNextVisit;
	}

	public static TreeNodeRecordEntity fromTreeNodeRecord(TreeNodeRecord treeMember) {
		return new TreeNodeRecordEntity(treeMember.getTreeNodeUrl(), treeMember.getTreeNodeStatus(),
				treeMember.getEarliestNextVisit());
	}

	public TreeNodeRecord toTreenode() {
		return new TreeNodeRecord(this.treeNodeUrl, this.treeNodeStatus, this.earliestNextVisit);
	}
}
