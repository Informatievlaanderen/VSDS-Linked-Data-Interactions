package ldes.client.treenodesupplier.repository.inmemory;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.repository.MemberRepository;

import java.util.*;
import java.util.stream.Stream;

public class InMemoryMemberRepository implements MemberRepository {

	private Queue<MemberRecord> members = new PriorityQueue<>();

	@Override
	public Optional<MemberRecord> getNextMember() {
		if (members.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(members.peek());
	}

	@Override
	public void deleteMember(MemberRecord member) {
		members.remove();
	}

	@Override
	public void saveTreeMembers(Stream<MemberRecord> treeMemberStream) {
		treeMemberStream.forEach(member -> members.offer(member));
	}

	@Override
	public void destroyState() {
		members = new PriorityQueue<>();
	}

}
