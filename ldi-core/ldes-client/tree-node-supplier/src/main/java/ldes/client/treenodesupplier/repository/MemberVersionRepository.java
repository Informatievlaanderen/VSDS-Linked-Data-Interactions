package ldes.client.treenodesupplier.repository;

import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;

/**
 * Repository that keeps track of the version-of and timestamp objects of the processed members
 */
public interface MemberVersionRepository {

	/**
	 * Saves the version-of and timestamp objects of a member to the repository
	 */
	void addMemberVersion(MemberVersionRecord memberVersion);

	/**
	 * Checks if the record with the version-of with the specified timestamp is after the last saved timestamp
	 * belonging to this version-of object, or if no other record with the specified version-of is already saved
	 *
	 * @param memberVersion the object containing the timestamp and version-of to check
	 * @return <code>true</code> if the timestamp of the record is after the last saved member or if this version-of is
	 * not yet saved, otherwise <code>false</code>
	 */
	boolean isVersionAfterTimestamp(MemberVersionRecord memberVersion);

	/**
	 * Clean up the repository when it is not used anymore
	 */
	void destroyState();

}
