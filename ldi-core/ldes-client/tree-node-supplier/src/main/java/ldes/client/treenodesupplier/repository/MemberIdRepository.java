package ldes.client.treenodesupplier.repository;

/**
 * Repository that keeps track of the id of the processed members
 */
public interface MemberIdRepository {

	/**
	 * Saves the memberId to the database if it does not already contain it
	 *
	 * @param memberId id of the member to save
	 * @return <code>true</code> if the member has already been saved, otherwise <code>false</code>
	 */
	boolean addMemberIdIfNotExists(String memberId);

	/**
	 * Cleans the database
	 */
	void destroyState();
}
