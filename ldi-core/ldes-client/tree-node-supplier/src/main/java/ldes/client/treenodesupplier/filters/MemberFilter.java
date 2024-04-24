package ldes.client.treenodesupplier.filters;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

public interface MemberFilter {
	boolean isAllowed(SuppliedMember memberId);

	void saveAllowedMember(SuppliedMember memberId);

	void destroyState();
}
