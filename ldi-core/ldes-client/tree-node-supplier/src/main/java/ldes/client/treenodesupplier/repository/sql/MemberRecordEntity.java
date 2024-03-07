package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(indexes = {
		@Index(name = "fn_index", columnList = "createdAt")
})
@NamedQuery(name = "Member.getFirstByMemberStatus", query = "SELECT m FROM MemberRecordEntity m WHERE m.memberStatus = :memberStatus ORDER BY m.createdAt")
@NamedQuery(name = "Member.countByMemberStatusAndId", query = "SELECT COUNT(m) FROM MemberRecordEntity m WHERE m.memberStatus = :memberStatus and m.id = :id")
@NamedQuery(name = "Member.deleteByMemberId", query = "DELETE m FROM MemberRecordEntity m WHERE m.id = :id")
public class MemberRecordEntity {

	@Id
	@Column(columnDefinition = "text", length = 10485760)
	private String id;

	private MemberStatus memberStatus;
	private LocalDateTime createdAt;

	@Column(name = "model", columnDefinition = "text", length = 10485760)
	private String modelAsString;

	public MemberRecordEntity() {
	}

	public MemberRecordEntity(String id, MemberStatus memberStatus, LocalDateTime dateCreated, String modelAsString) {
		this.id = id;
		this.memberStatus = memberStatus;
		this.createdAt = dateCreated;
		this.modelAsString = modelAsString;
	}

	public static MemberRecordEntity fromMemberRecord(MemberRecord treeMember) {
		final Model model = treeMember.getModel();
		final String localModalString = model != null ? RDFWriter.source(model).lang(Lang.NQUADS).asString() : null;
		return new MemberRecordEntity(treeMember.getMemberId(), treeMember.getMemberStatus(), treeMember.getCreatedAt(), localModalString);
	}

	public MemberRecord toMemberRecord() {
		final Model model = RDFParserBuilder.create().fromString(modelAsString).lang(Lang.NQUADS).toModel();
		return new MemberRecord(id, model, memberStatus, createdAt);
	}

}
