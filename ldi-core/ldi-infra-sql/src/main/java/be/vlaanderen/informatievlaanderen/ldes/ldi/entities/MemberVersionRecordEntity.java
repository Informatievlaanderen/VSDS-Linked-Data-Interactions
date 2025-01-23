package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_version")
@NamedQuery(name = "MemberVersion.findMemberVersionAfterTimestamp", query = "SELECT m FROM MemberVersionRecordEntity m WHERE versionOf = :versionOf AND timestamp >= :timestamp")
public class MemberVersionRecordEntity {
    @Id
    private String versionOf;

    @Column
    private LocalDateTime timestamp;

    public MemberVersionRecordEntity() {
    }

    public MemberVersionRecordEntity(String versionOf, LocalDateTime timestamp) {
        this.versionOf = versionOf;
        this.timestamp = timestamp;
    }

    public String getVersionOf() {
        return versionOf;
    }

	public void setVersionOf(String versionOf) {
		this.versionOf = versionOf;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
