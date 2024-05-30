package ldes.client.treenodesupplier.filters;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

/**
 * Filter interface to filter out some supplied members
 */
public interface MemberFilter {
	/**
	 * Checks whether the supplied member can go through the filter
	 *
	 * @param member member under filtration test
	 * @return <code>true</code> if the member can go through the filter, otherwise false
	 */
	boolean isAllowed(SuppliedMember member);

	/**
	 * Saves the newly allowed member, as this can affect the filtration process
	 *
	 * @param member member to be saved
	 */
	void saveAllowedMember(SuppliedMember member);

	/**
	 * Release resources when the filter is not required anymore
	 */
	void destroyState();
}
