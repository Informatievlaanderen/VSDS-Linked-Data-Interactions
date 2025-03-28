package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import javax.persistence.*;

@Entity
@Table(name = "member_hashed")
@NamedQuery(name = "HashedStateMember.findMember", query = "FROM HashedStateMemberEntity m WHERE m.id = :memberId AND m.hash = :memberHash")
@NamedNativeQuery(name = "HashedStateMember.insert", query = "INSERT INTO member_hashed(id, hash) VALUES (?, ?) ON CONFLICT DO NOTHING")
public class HashedStateMemberEntity {
	@Id
	private String id;
	@Column
	private String hash;

	public HashedStateMemberEntity() {
	}

	public HashedStateMemberEntity(String id, String hash) {
		this.id = id;
		this.hash = hash;
	}
}
