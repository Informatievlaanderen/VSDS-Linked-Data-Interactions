package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(indexes = {
		@Index(name="treenoderecordentity_treenodeurl_treenodestatus", columnList = "treeNodeUrl,treeNodeStatus"),
		@Index(name="treenoderecordentity_treenodestatus_earliestnextvisit", columnList = "treeNodeStatus,earliestNextVisit")
})
@NamedQuery(name = "TreeNode.getAll", query = "SELECT t FROM TreeNodeRecordEntity t")
@NamedQuery(name = "TreeNode.getById", query = "SELECT t FROM TreeNodeRecordEntity t WHERE t.id = :id")
@NamedQuery(name = "TreeNode.getByIdAndStatus", query = "SELECT t FROM TreeNodeRecordEntity t WHERE t.id = :id and t.treeNodeStatus = :treeNodeStatus")
@NamedQuery(name = "TreeNode.getByStatusAndDate", query = "SELECT t FROM TreeNodeRecordEntity t WHERE t.treeNodeStatus = :treeNodeStatus ORDER BY t.earliestNextVisit")
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
