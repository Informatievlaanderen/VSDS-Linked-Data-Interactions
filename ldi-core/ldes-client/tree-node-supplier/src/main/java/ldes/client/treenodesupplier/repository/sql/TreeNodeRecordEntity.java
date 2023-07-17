package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "TreeNode.count", query = "SELECT COUNT(t) FROM TreeNodeRecordEntity t")
@NamedQuery(name = "TreeNode.countById", query = "SELECT COUNT(t) FROM TreeNodeRecordEntity t WHERE t.id = :id")
@NamedQuery(name = "TreeNode.countByIdAndStatus", query = "SELECT COUNT(t) FROM TreeNodeRecordEntity t WHERE t.id = :id and t.treeNodeStatus = :treeNodeStatus")
@NamedQuery(name = "TreeNode.getByTreeNodeStatus", query = "SELECT t FROM TreeNodeRecordEntity t WHERE t.treeNodeStatus = :treeNodeStatus")
public class TreeNodeRecordEntity {

	@Id
	@Column(columnDefinition = "text", length = 10485760)
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

	public TreeNodeRecord toTreeNode() {
		return new TreeNodeRecord(this.treeNodeUrl, this.treeNodeStatus, this.earliestNextVisit);
	}
}
