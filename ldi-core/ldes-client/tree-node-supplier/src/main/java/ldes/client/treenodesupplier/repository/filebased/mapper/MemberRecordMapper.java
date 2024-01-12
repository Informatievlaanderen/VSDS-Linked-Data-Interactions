package ldes.client.treenodesupplier.repository.filebased.mapper;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MemberRecordMapper {

	public static final String DELIMITER = ";;;";
	private DateTimeFormatter dateTimeFormatter;

	public MemberRecordMapper() {
		dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
	}

	public String fromMemberRecord(MemberRecord memberRecord) {
		if (memberRecord.getModel() != null) {
			return String.join(DELIMITER, memberRecord.getMemberId(), memberRecord.getMemberStatus().toString(), dateTimeFormatter.format(memberRecord.getCreatedAt()),
					RDFWriter.source(memberRecord.getModel()).lang(Lang.NQUADS).asString().replace("\n", "#NEWLINE"));
		} else {
			return String.join(DELIMITER, memberRecord.getMemberId(), memberRecord.getMemberStatus().toString(), dateTimeFormatter.format(memberRecord.getCreatedAt()));
		}

	}

	public MemberRecord toMemberRecord(String line) {
		String[] parts = line.split(DELIMITER);
		Model model = null;
		if (parts.length > 3) {
			model = RDFParserBuilder.create().fromString(parts[3].replace("#NEWLINE", "\n")).lang(Lang.NQUADS)
					.toModel();
		}
		return new MemberRecord(parts[0], model, MemberStatus.valueOf(parts[1]), LocalDateTime.from(dateTimeFormatter.parse(parts[2])));
	}
}
