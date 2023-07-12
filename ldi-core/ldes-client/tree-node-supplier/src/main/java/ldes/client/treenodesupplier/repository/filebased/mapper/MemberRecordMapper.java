package ldes.client.treenodesupplier.repository.filebased.mapper;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

public class MemberRecordMapper {

	public static final String DELIMITER = ";;;";

	public String fromMemberRecord(MemberRecord memberRecord) {
		if (memberRecord.getModel() != null) {
			return String.join(DELIMITER, memberRecord.getMemberId(), memberRecord.getMemberStatus().toString(),
					RDFWriter.source(memberRecord.getModel()).lang(Lang.NQUADS).asString().replace("\n", "#NEWLINE"));
		} else {
			return String.join(DELIMITER, memberRecord.getMemberId(), memberRecord.getMemberStatus().toString());
		}

	}

	public MemberRecord toMemberRecord(String line) {
		String[] parts = line.split(DELIMITER);
		Model model = null;
		if (parts.length > 2) {
			model = RDFParserBuilder.create().fromString(parts[2].replace("#NEWLINE", "\n")).lang(Lang.NQUADS)
					.toModel();
		}
		return new MemberRecord(parts[0], model, MemberStatus.valueOf(parts[1]));
	}
}
