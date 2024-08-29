package ldes.client.treenodesupplier.repository.mapper;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.MemberRecordEntity;
import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MemberRecordEntityMapper {
	private MemberRecordEntityMapper() {
	}

	public static MemberRecordEntity fromMemberRecord(MemberRecord treeMember) {
		final Model model = treeMember.getModel();
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            RDFWriter.source(model).lang(Lang.RDFPROTO).output(stream);
            stream.flush();
			final byte[] bytes = stream.toByteArray();
			return new MemberRecordEntity(treeMember.getMemberId(), treeMember.getCreatedAt(), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	public static MemberRecord toMemberRecord(MemberRecordEntity memberRecordEntity) {
		final Model model = RDFParser.source(new ByteArrayInputStream(memberRecordEntity.getModelAsBytes())).lang(Lang.RDFPROTO).toModel();
		return new MemberRecord(memberRecordEntity.getMemberId(), model, memberRecordEntity.getCreatedAt());
	}
}
