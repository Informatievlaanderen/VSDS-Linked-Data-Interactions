package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table()
@NamedQuery(name = "MemberVersion.findMemberVersionAfterTimestamp", query = "SELECT m FROM MemberVersionRecordEntity m WHERE versionOf = :versionOf AND timestamp > :timestamp")
public class MemberVersionRecordEntity {
    @Id
    private String versionOf;

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public static MemberVersionRecordEntity fromMemberVersionRecord(MemberVersionRecord memberVersionRecord) {
        return new MemberVersionRecordEntity(memberVersionRecord.getVersionOf(), memberVersionRecord.getTimestamp());
    }
}
