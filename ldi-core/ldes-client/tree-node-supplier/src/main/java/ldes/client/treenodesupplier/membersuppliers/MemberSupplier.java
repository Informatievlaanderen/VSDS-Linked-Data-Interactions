package ldes.client.treenodesupplier.membersuppliers;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

import java.util.function.Supplier;

/**
 * Interface that supply the fetched members of an LDES
 */
public interface MemberSupplier extends Supplier<SuppliedMember> {
	/**
	 * Initialization and/or set up of some processes or resources to supply the fetched members
	 */
	void init();

	/**
	 * Release resources when the filter is not required anymore
	 */
	void destroyState();
}
