package ldes.client.treenodesupplier;

import ldes.client.treenodefetcher.domain.entities.TreeMember;

import java.util.Optional;

public interface MemberRepository {

	Optional<TreeMember> getUnprocessedTreeMember();

	void addUnprocessedTreeMember(TreeMember treeMember);

	boolean isProcessed(TreeMember member);

	void addProcessedTreeMember(TreeMember treeMember);

}
