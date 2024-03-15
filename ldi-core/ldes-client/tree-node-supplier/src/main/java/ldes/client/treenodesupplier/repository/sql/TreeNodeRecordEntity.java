package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
		@Index(name = "ix_status", columnList = "treeNodeStatus ASC")
})
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
