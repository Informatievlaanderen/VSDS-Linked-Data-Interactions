package ldes.client.treenodesupplier.repository.sql;

import javax.persistence.*;

@Entity
@Table()
@NamedQuery(name = "MemberId.get", query = "SELECT m FROM MemberIdRecordEntity m WHERE m.id = :id")
public class MemberIdRecordEntity {
    @Id
    private String id;

    public MemberIdRecordEntity() {
    }
    public MemberIdRecordEntity(String id) {
        this.id = id;
    }

    public static MemberIdRecordEntity fromId(String id) {
        return new MemberIdRecordEntity(id);
    }
}
