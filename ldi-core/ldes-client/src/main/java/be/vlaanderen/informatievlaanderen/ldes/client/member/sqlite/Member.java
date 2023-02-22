package be.vlaanderen.informatievlaanderen.ldes.client.member.sqlite;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "Member.countAll", query = "SELECT COUNT(m) FROM Member m")
@NamedQuery(name = "Member.countById", query = "SELECT COUNT(m) FROM Member m WHERE m.id = :id")
@NamedQuery(name = "Member.deleteAll", query = "DELETE FROM Member")
public class Member {
	@Id
	private final String id;

	public Member(String id) {
		this.id = id;
	}
}
