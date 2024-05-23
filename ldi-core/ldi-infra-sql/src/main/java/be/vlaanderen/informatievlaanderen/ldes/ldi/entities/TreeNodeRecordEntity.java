package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

@Entity
@NamedQuery(name = "TreeNode.count", query = "SELECT COUNT(t) FROM TreeNodeRecordEntity t")
@NamedQuery(name = "TreeNode.countById", query = "SELECT COUNT(t) FROM TreeNodeRecordEntity t WHERE t.id = :id")
@NamedQuery(name = "TreeNode.countByIdAndStatus", query = "SELECT COUNT(t) FROM TreeNodeRecordEntity t WHERE t.id = :id and t.treeNodeStatus = :treeNodeStatus")
@NamedQuery(name = "TreeNode.getByTreeNodeStatus", query = "SELECT t FROM TreeNodeRecordEntity t WHERE t.treeNodeStatus = :treeNodeStatus")
public class TreeNodeRecordEntity {

	@Id
	@Column(columnDefinition = "text", length = 10485760)
	private String treeNodeUrl;
	private String treeNodeStatus;
	private LocalDateTime earliestNextVisit;
	@Column
	@ElementCollection(targetClass=String.class)
	private List<String> members;

	public TreeNodeRecordEntity() {
	}

	public TreeNodeRecordEntity(String treeNodeUrl, String treeNodeStatus, LocalDateTime earliestNextVisit, List<String> members) {
		this.treeNodeUrl = treeNodeUrl;
		this.treeNodeStatus = treeNodeStatus;
		this.earliestNextVisit = earliestNextVisit;
		this.members = members;
	}

	public String getTreeNodeUrl() {
		return treeNodeUrl;
	}

	public String getTreeNodeStatus() {
		return treeNodeStatus;
	}

	public LocalDateTime getEarliestNextVisit() {
		return earliestNextVisit;
	}

	public List<String> getMembers() {
		return members;
	}
}
