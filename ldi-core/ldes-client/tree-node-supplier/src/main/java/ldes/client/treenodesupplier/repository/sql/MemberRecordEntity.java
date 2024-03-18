package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@NamedQuery(name = "Member.getFirst", query = "SELECT m FROM MemberRecordEntity m ORDER BY m.createdAt")
@NamedQuery(name = "Member.deleteByMemberId", query = "DELETE FROM MemberRecordEntity WHERE memberId = :memberId")
public class MemberRecordEntity {

	@Id
	@GeneratedValue
	private int id;
	@Column(columnDefinition = "text", length = 10485760)
	private String memberId;
	private LocalDateTime createdAt;

	@Column(name = "model", columnDefinition = "text", length = 10485760)
	private String modelAsString;

	public MemberRecordEntity() {
	}

	public MemberRecordEntity(String memberId, LocalDateTime dateCreated, String modelAsString) {
		this.memberId = memberId;
		this.createdAt = dateCreated;
		this.modelAsString = modelAsString;
	}

	public static MemberRecordEntity fromMemberRecord(MemberRecord treeMember) {
		final Model model = treeMember.getModel();
		final String localModalString = model != null ? RDFWriter.source(model).lang(Lang.NQUADS).asString() : null;
		return new MemberRecordEntity(treeMember.getMemberId(), treeMember.getCreatedAt(), localModalString);
	}

	public MemberRecord toMemberRecord() {
		final Model model = RDFParserBuilder.create().fromString(modelAsString).lang(Lang.NQUADS).toModel();
		return new MemberRecord(memberId, model, createdAt);
	}

}
