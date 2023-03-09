package ldes.client.treenodesupplier.repository;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;

import java.util.Optional;

public interface MemberRepository {

	Optional<MemberRecord> getUnprocessedTreeMember();

	boolean isProcessed(MemberRecord member);

	void saveTreeMember(MemberRecord treeMember);

	void destroyState();
}
