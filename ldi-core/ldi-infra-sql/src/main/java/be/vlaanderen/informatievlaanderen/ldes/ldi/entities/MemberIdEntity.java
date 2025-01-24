package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

@Entity
@Table(name = "member_id")
@NamedNativeQuery(name = "MemberId.insert", query = "INSERT INTO member_id(id) VALUES (:memberId) ON CONFLICT DO NOTHING")
public class MemberIdEntity {
	@Id
	private String id;

	public MemberIdEntity() {
		// Default constructor needed for JPA
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
