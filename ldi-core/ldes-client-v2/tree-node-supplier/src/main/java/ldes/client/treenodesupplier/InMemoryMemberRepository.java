package ldes.client.treenodesupplier;

import ldes.client.treenodefetcher.domain.entities.TreeMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryMemberRepository implements MemberRepository {

	List<TreeMember> unprocessed = new ArrayList<>();
	List<String> processed = new ArrayList<>();

	public Optional<TreeMember> getUnprocessedTreeMember() {
		if (unprocessed.isEmpty())
			return Optional.empty();
		return Optional.of(unprocessed.remove(0));
	}

	public void addUnprocessedTreeMember(TreeMember treeMember) {
		unprocessed.add(treeMember);
	}

	public boolean isProcessed(TreeMember member) {
		return processed.contains(member.getMemberId());
	}

	public void addProcessedTreeMember(TreeMember treeMember) {
		processed.add(treeMember.getMemberId());
	}
}
