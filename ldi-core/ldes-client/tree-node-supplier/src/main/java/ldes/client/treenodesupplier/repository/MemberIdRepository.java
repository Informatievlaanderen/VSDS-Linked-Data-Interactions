package ldes.client.treenodesupplier.repository;

/**
 * Repository that keeps track of the id of the processed members
 */
public interface MemberIdRepository {
	/**
	 * Saves the memberId to the database
	 *
	 * @param memberId id of the member to save
	 */
	void addMemberId(String memberId);

	/**
	 * Checks whether the id of the member already has been processed
	 * @param memberId id of the member to be checked
	 * @return <code>true</code> if the member already has been saved, otherwise <code>false</code>
	 */
	boolean contains(String memberId);

	/**
	 * Cleans the database
	 */
	void destroyState();
}
