package ldes.client.treenodesupplier.filters;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

public interface MemberFilter {
	boolean isAllowed(SuppliedMember member);

	void saveAllowedMember(SuppliedMember member);

	void destroyState();
}
