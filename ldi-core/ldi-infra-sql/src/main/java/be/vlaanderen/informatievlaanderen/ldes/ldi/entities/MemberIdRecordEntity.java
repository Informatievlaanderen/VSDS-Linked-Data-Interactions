package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import jakarta.persistence.*;

@Entity
@Table
@NamedNativeQuery(name = "MemberId.insert", query = "INSERT INTO MemberIdRecordEntity(id) VALUES (:memberId) ON CONFLICT DO NOTHING")
@Cacheable(value = false)
public class MemberIdRecordEntity {
	@Id
	private String id;
}
