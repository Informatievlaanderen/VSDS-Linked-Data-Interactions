package ldes.client.treenodesupplier.repository.sqlite;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

import javax.persistence.Column;
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

	@Column(name = "model")
	private String modelAsString;

	public MemberRecordEntity() {
	}

	public MemberRecordEntity(String id, MemberStatus memberStatus, String modelAsString) {
		this.id = id;
		this.memberStatus = memberStatus;
		this.modelAsString = modelAsString;
	}

	public static MemberRecordEntity fromMemberRecord(MemberRecord treeMember) {
		final Model model = treeMember.getModel();
		final String localModalString = model != null ? RDFWriter.source(model).lang(Lang.NQUADS).asString() : null;
		return new MemberRecordEntity(treeMember.getMemberId(), treeMember.getMemberStatus(), localModalString);
	}

	public MemberRecord toMemberRecord() {
		final Model model = RDFParserBuilder.create().fromString(modelAsString).lang(Lang.NQUADS).toModel();
		return new MemberRecord(id, model, memberStatus);
	}
}
