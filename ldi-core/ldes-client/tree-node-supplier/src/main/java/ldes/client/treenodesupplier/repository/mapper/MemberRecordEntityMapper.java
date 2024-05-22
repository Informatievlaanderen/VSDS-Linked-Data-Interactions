package ldes.client.treenodesupplier.repository.mapper;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.MemberRecordEntity;
import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

public class MemberRecordEntityMapper {
	private MemberRecordEntityMapper() {
	}

	public static MemberRecordEntity fromMemberRecord(MemberRecord treeMember) {
		final Model model = treeMember.getModel();
		final String localModalString = model != null ? RDFWriter.source(model).lang(Lang.TURTLE).asString() : null;
		return new MemberRecordEntity(treeMember.getMemberId(), treeMember.getCreatedAt(), localModalString);
	}

	public static MemberRecord toMemberRecord(MemberRecordEntity memberRecordEntity) {
		final Model model = RDFParserBuilder.create().fromString(memberRecordEntity.getModelAsString()).lang(Lang.TURTLE).toModel();
		return new MemberRecord(memberRecordEntity.getMemberId(), model, memberRecordEntity.getCreatedAt());
	}
}
