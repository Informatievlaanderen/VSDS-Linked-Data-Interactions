package ldes.client.treenodesupplier.repository;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;

import java.util.Optional;
import java.util.Set;

public interface MemberRepository {

	Optional<MemberRecord> getNextUnprocessedMember();

	void markAsProcessed(MemberRecord member);

	void insertTreeMembers(Set<MemberRecord> treeMemberStream);

	void destroyState();

	void deleteProcessedMembersByTreeNode(String treeNodeUrl);

	Set<String> findMemberIdsByTreeNode(String treeNodeUrl);

}
