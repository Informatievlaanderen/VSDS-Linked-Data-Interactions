package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql.entities;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;

import javax.persistence.*;

@Entity
@Table
@NamedQuery(name = "HashedStateMember.findMember", query = "FROM HashedStateMemberEntity m WHERE m.id = :memberId AND m.hash = :memberHash")
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

	public static HashedStateMemberEntity fromHashedStateMember(HashedStateMember hashedStateMember) {
		return new HashedStateMemberEntity(hashedStateMember.memberId(), hashedStateMember.memberHash());
	}
}
