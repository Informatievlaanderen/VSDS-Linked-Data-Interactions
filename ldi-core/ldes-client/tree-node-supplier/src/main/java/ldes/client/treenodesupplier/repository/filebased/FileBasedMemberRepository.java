package ldes.client.treenodesupplier.repository.filebased;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.filebased.mapper.MemberRecordMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileBasedMemberRepository implements MemberRepository {
	public static final String UNPROCESSED_MEMBERS = "membersUnprocessed.txt";
	public static final String PROCESSED_MEMBERS = "membersProcessed.txt";

	private final FileManagerFactory fileManagerFactory = FileManagerFactory.getInstance();
	private final FileManager fileManager = fileManagerFactory.getFileManager();
	private final MemberRecordMapper mapper = new MemberRecordMapper();

	@Override
	public Optional<MemberRecord> getUnprocessedTreeMember() {
		return fileManager
				.getRecords(UNPROCESSED_MEMBERS)
				.map(mapper::toMemberRecord)
				.findFirst();
	}

	@Override
	public boolean isProcessed(MemberRecord member) {
		return fileManager
				.getRecords(PROCESSED_MEMBERS)
				.map(mapper::toMemberRecord)
				.anyMatch(memberRecord -> memberRecord.equals(member));
	}

	@Override
	public void saveTreeMember(MemberRecord treeMember) {
		if (treeMember.getMemberStatus() == MemberStatus.PROCESSED) {
			fileManager.appendRecord(PROCESSED_MEMBERS, mapper.fromMemberRecord(treeMember));
			removeMemberFromUnprocessed(treeMember);
		} else if (treeMember.getMemberStatus() == MemberStatus.UNPROCESSED) {
			fileManager.appendRecord(UNPROCESSED_MEMBERS, mapper.fromMemberRecord(treeMember));
		}
	}

	private void removeMemberFromUnprocessed(MemberRecord treeMember) {
		List<MemberRecord> unprocessed = fileManager.getRecords(UNPROCESSED_MEMBERS).map(mapper::toMemberRecord)
				.collect(Collectors.toList());
		unprocessed.remove(treeMember);
		fileManager.createNewRecords(UNPROCESSED_MEMBERS, unprocessed.stream().map(mapper::fromMemberRecord));
	}

	@Override
	public void destroyState() {
		FileManagerFactory.destroyState();
	}
}
