package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import javax.persistence.*;

@Entity
@Table
@NamedNativeQuery(name = "HashedStateMember.insert", query = "INSERT INTO HashedStateMemberEntity(id, hash) VALUES (?, ?)")
public class HashedStateMemberEntity {
	@Id
	private String id;
	@Column
	private String hash;
}
