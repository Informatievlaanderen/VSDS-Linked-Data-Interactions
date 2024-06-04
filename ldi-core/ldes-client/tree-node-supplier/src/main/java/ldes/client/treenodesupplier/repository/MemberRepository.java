package ldes.client.treenodesupplier.repository;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * * Repository that keeps track of the processed members
 */
public interface MemberRepository {

	/**
	 * @return the first MemberRecord in the repository
	 */
	Optional<MemberRecord> getTreeMember();

	/**
	 * @param member MemberRecord to delete from the repository
	 */
	void deleteMember(MemberRecord member);

	/**
	 * @param treeMemberStream the stream of MemberRecords to save
	 */
	void saveTreeMembers(Stream<MemberRecord> treeMemberStream);

	/**
	 * Clean up the repository when it is not used anymore
	 */
	void destroyState();
}
