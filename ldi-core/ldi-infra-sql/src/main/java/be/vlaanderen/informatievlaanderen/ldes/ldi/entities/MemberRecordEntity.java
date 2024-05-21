package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
		@Index(name = "fn_index", columnList = "createdAt")
})
@NamedQuery(name = "Member.getFirst", query = "SELECT m FROM MemberRecordEntity m ORDER BY m.createdAt")
@NamedQuery(name = "Member.deleteByMemberId", query = "DELETE FROM MemberRecordEntity WHERE memberId = :memberId")
public class MemberRecordEntity {

	@Id
	@GeneratedValue
	private int id;
	@Column(columnDefinition = "text", length = 10485760)
	private String memberId;
	private LocalDateTime createdAt;

	@Column(name = "model", columnDefinition = "text", length = 10485760)
	private String modelAsString;

	public MemberRecordEntity() {
	}

	public MemberRecordEntity(String memberId, LocalDateTime dateCreated, String modelAsString) {
		this.memberId = memberId;
		this.createdAt = dateCreated;
		this.modelAsString = modelAsString;
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

	public String getModelAsString() {
		return modelAsString;
	}
}
