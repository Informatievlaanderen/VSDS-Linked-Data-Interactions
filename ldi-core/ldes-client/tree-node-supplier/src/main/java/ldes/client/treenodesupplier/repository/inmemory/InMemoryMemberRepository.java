package ldes.client.treenodesupplier.repository.inmemory;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.repository.MemberRepository;

import java.util.*;

public class InMemoryMemberRepository implements MemberRepository {

	private Queue<MemberRecord> membersToProcess = new PriorityQueue<>();
	private Map<String, Set<MemberRecord>> processedMembers = new HashMap<>();

	@Override
	public Optional<MemberRecord> getNextUnprocessedMember() {
		if (membersToProcess.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(membersToProcess.peek());
	}

	@Override
	public void markAsProcessed(MemberRecord member) {
		membersToProcess.remove();
		processedMembers.computeIfAbsent(member.getTreeNodeUrl(), k -> new HashSet<>()).add(member);
	}

	@Override
	public void insertTreeMembers(Set<MemberRecord> treeMemberStream) {
		treeMemberStream.forEach(member -> membersToProcess.offer(member));
	}

	@Override
	public void destroyState() {
		membersToProcess = new PriorityQueue<>();
	}

	@Override
	public void deleteProcessedMembersByTreeNode(String treeNodeUrl) {
		processedMembers.remove(treeNodeUrl);
	}

	@Override
	public Set<String> findMemberIdsByTreeNode(String treeNodeUrl) {
		Set<String> ids = new HashSet<>();
		processedMembers.get(treeNodeUrl).forEach(member -> ids.add(member.getMemberId()));
		membersToProcess.forEach(member -> {
			if (member.getTreeNodeUrl().equals(treeNodeUrl)) {
				ids.add(member.getMemberId());
			}
		});
		return ids;
	}

}
