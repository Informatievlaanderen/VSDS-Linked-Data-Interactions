package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table()
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
}
