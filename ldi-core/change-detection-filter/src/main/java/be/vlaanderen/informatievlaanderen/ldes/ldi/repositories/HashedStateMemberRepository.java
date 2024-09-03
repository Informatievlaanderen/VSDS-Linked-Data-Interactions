package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;

/**
 * Repository to maintain the HashedStateMember objects into the database
 */
public interface HashedStateMemberRepository {
	/**
	 * Saves the record if the database does contain a record with the exact same HashedStateMember yet
	 *
	 * @param hashedStateMember representation object that contains a memberId and the hash of the state member
	 * @return <code>true</code> if the member is saved successfully, otherwise <code>false</code>
	 */
	boolean saveHashedStateMemberIfNotExists(HashedStateMember hashedStateMember);

	/**
	 * Clean up the resources, should be called when the repository is not used anymore
	 */
	void destroyState();
}
