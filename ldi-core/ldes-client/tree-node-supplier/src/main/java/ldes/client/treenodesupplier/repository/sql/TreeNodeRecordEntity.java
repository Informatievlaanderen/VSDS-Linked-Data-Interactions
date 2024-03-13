package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(indexes = {
		@Index(name = "ix_status", columnList = "treeNodeStatus ASC")
})
@NamedQuery(name = "TreeNode.count", query = "SELECT COUNT(t) FROM TreeNodeRecordEntity t")
@NamedQuery(name = "TreeNode.countById", query = "SELECT COUNT(t) FROM TreeNodeRecordEntity t WHERE t.id = :id")
@NamedQuery(name = "TreeNode.countByIdAndStatus", query = "SELECT COUNT(t) FROM TreeNodeRecordEntity t WHERE t.id = :id and t.treeNodeStatus = :treeNodeStatus")
public class TreeNodeRecordEntity {

	@Id
	@Column(columnDefinition = "text", length = 10485760)
	private String treeNodeUrl;
	private TreeNodeStatus treeNodeStatus;
	private LocalDateTime earliestNextVisit;
	@Column
	@ElementCollection(targetClass=String.class)
	private List<String> members;

	public TreeNodeRecordEntity() {
	}

	public TreeNodeRecordEntity(String treeNodeUrl, TreeNodeStatus treeNodeStatus, LocalDateTime earliestNextVisit, List<String> members) {
		this.treeNodeUrl = treeNodeUrl;
		this.treeNodeStatus = treeNodeStatus;
		this.earliestNextVisit = earliestNextVisit;
		this.members = members;
	}

	public static TreeNodeRecordEntity fromTreeNodeRecord(TreeNodeRecord treeMember) {
		return new TreeNodeRecordEntity(treeMember.getTreeNodeUrl(), treeMember.getTreeNodeStatus(),
				treeMember.getEarliestNextVisit(), treeMember.getMemberIds());
	}

	public TreeNodeRecord toTreeNode() {
		return new TreeNodeRecord(this.treeNodeUrl, this.treeNodeStatus, this.earliestNextVisit, this.members);
	}
}
