package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;

/**
 * Repository to maintain the HashedStateMember objects into the database
 */
public interface HashedStateMemberRepository {
	/**
	 * Checks if the database already contain a record that contains the exact same HashedStateMember
	 *
	 * @param hashedStateMember representation object that contains a memberId and the hash of the state member
	 * @return true if an exact same record has found, otherwise false
	 */
	boolean containsHashedStateMember(HashedStateMember hashedStateMember);

	void saveHashedStateMember(HashedStateMember hashedStateMember);
	/**
	 * Clean up the resources, should be called when the repository is not used anymore
	 */
	void close();
}
