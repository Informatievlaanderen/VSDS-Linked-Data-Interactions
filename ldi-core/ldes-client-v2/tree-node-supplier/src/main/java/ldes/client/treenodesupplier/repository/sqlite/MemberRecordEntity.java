package ldes.client.treenodesupplier.repository.sqlite;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "Member.getByMemberStatus", query = "SELECT m FROM MemberRecordEntity m WHERE m.memberStatus = :memberStatus")
@NamedQuery(name = "Member.countByMemberStatusAndId", query = "SELECT COUNT(m) FROM MemberRecordEntity m WHERE m.memberStatus = :memberStatus and m.id = :id")
public class MemberRecordEntity {
	@Id
	private String id;
	private MemberStatus memberStatus;
	private String model;

	public MemberRecordEntity() {
	}

	public MemberRecordEntity(String id, MemberStatus memberStatus, String model) {
		this.id = id;
		this.memberStatus = memberStatus;
		this.model = model;
	}

	public static MemberRecordEntity fromMemberRecord(MemberRecord treeMember) {
		String ldesMemberString = null;
		if (treeMember.getModel() != null) {
			ldesMemberString = RDFWriter.source(treeMember.getModel()).lang(Lang.NQUADS).asString();
		}
		return new MemberRecordEntity(treeMember.getMemberId(), treeMember.getMemberStatus(), ldesMemberString);
	}

	public MemberRecord toMemberRecord() {
		return new MemberRecord(this.id, RDFParserBuilder.create().fromString(this.model).lang(Lang.NQUADS).toModel(),
				this.memberStatus);
	}
}
