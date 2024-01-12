package ldes.client.treenodesupplier.repository.inmemory;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import ldes.client.treenodesupplier.repository.MemberRepository;

import java.util.*;
import java.util.stream.Stream;

public class InMemoryMemberRepository implements MemberRepository {

	private Queue<MemberRecord> unprocessed = new PriorityQueue<>();
	private List<MemberRecord> processed = new ArrayList<>();

	@Override
	public Optional<MemberRecord> getUnprocessedTreeMember() {
		if (unprocessed.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(unprocessed.poll());
	}

	@Override
	public boolean isProcessed(MemberRecord member) {
		return processed.contains(member);
	}

	@Override
	public void saveTreeMembers(Stream<MemberRecord> treeMemberStream) {
		treeMemberStream.forEach(this::saveTreeMember);
	}

	@Override
	public void saveTreeMember(MemberRecord treeMember) {
		if (treeMember.getMemberStatus() == MemberStatus.PROCESSED) {
			processed.add(treeMember);
		} else if (treeMember.getMemberStatus() == MemberStatus.UNPROCESSED) {
			unprocessed.offer(treeMember);
		}
	}

	@Override
	public void destroyState() {
		processed = new ArrayList<>();
		unprocessed = new PriorityQueue<>();
	}

}
