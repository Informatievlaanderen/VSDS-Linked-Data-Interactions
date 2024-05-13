package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql.entities;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;

import javax.persistence.*;

@Entity
@Table
@NamedQuery(name = "HashedStateMember.getById", query = "SELECT member FROM HashedStateMemberEntity member WHERE member.id = :memberId")
@NamedQuery(name = "HashedStateMember.containsMember", query = "SELECT EXISTS(SELECT member FROM HashedStateMemberEntity member WHERE member.id = :memberId AND member.hash = :memberHash)")
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

	public HashedStateMember toHashedStateMember() {
		return new HashedStateMember(id, hash);
	}
}
