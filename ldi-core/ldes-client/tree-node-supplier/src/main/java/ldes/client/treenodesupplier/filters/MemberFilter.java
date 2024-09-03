package ldes.client.treenodesupplier.filters;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

/**
 * Filter interface to filter out some supplied members
 */
public interface MemberFilter {
	/**
	 * Saves the member if it is allowed to supply the member
	 *
	 * @param member the members that will be checked if it is allowed to supply and saves it if so
	 * @return whether the member is allowed and saved
	 */
	boolean saveMemberIfAllowed(SuppliedMember member);

	/**
	 * Release resources when the filter is not required anymore
	 */
	void destroyState();
}
