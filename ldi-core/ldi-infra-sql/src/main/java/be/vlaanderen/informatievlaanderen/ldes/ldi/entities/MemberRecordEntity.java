package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
		@Index(name = "idx_members_created_at", columnList = "createdAt"),
		@Index(name = "idx_members_member_id", columnList = "memberId")
})
@NamedQuery(name = "Member.getAllOrderedByCreation", query = "SELECT m FROM MemberRecordEntity m ORDER BY m.createdAt")
@NamedQuery(name = "Member.deleteByMemberId", query = "DELETE FROM MemberRecordEntity WHERE memberId = :memberId")
public class MemberRecordEntity {

	@Id
	@GeneratedValue
	private int id;
	@Column(columnDefinition = "text", length = 10485760)
	private String memberId;
	private LocalDateTime createdAt;

	@Column(name = "model", columnDefinition = "bytea", nullable = false)
	private byte[] bytes;

	public MemberRecordEntity() {
	}

	public MemberRecordEntity(String memberId, LocalDateTime dateCreated, byte[] bytes) {
		this.memberId = memberId;
		this.createdAt = dateCreated;
		this.bytes = bytes;
	}

	public int getId() {
		return id;
	}

	public String getMemberId() {
		return memberId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public byte[] getModelAsBytes() {
		return bytes;
	}
}
