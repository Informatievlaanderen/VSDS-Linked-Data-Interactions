package ldes.client.treenodesupplier.repository.mapper;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.MemberVersionRecordEntity;
import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;

public class MemberVersionRecordEntityMapper {
	private MemberVersionRecordEntityMapper() {
	}

	public static MemberVersionRecordEntity fromMemberVersionRecord(MemberVersionRecord memberVersionRecord) {
		return new MemberVersionRecordEntity(memberVersionRecord.getVersionOf(), memberVersionRecord.getTimestamp());
	}
}
