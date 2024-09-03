package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

@Entity
@Table
@NamedNativeQuery(name = "MemberId.insert", query = "INSERT INTO MemberIdRecordEntity(id) VALUES (:memberId) ON CONFLICT DO NOTHING")
public class MemberIdRecordEntity {
	@Id
	private String id;
}
