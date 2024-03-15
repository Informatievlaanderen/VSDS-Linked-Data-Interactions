package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
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
public class MemberRecordEntity {

	@Id
	@Column(columnDefinition = "text", length = 10485760)
	private String id;

	private LocalDateTime createdAt;

	@Column(name = "model", columnDefinition = "text", length = 10485760)
	private String modelAsString;

	private String treeNodeUrl;

	private boolean processed;

	public MemberRecordEntity() {
	}

	public MemberRecordEntity(String id,
							  LocalDateTime dateCreated,
							  String modelAsString,
							  String treeNodeUrl,
							  boolean processed) {
		this.id = id;
		this.createdAt = dateCreated;
		this.modelAsString = modelAsString;
		this.treeNodeUrl = treeNodeUrl;
		this.processed = processed;
	}

	public static MemberRecordEntity fromMemberRecord(MemberRecord treeMember) {
		final Model model = treeMember.getModel();
		final String localModalString = model != null ? RDFWriter.source(model).lang(Lang.NQUADS).asString() : null;
		return new MemberRecordEntity(treeMember.getMemberId(), treeMember.getCreatedAt(), localModalString,
				treeMember.getTreeNodeUrl(), treeMember.isProcessed());
	}

	public MemberRecord toMemberRecord() {
		final Model model = RDFParserBuilder.create().fromString(modelAsString).lang(Lang.NQUADS).toModel();
		return new MemberRecord(id, model, createdAt, treeNodeUrl, processed);
	}

}
