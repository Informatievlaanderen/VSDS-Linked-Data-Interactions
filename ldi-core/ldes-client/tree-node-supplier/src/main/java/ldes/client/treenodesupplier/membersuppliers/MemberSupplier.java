package ldes.client.treenodesupplier.membersuppliers;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

import java.util.function.Supplier;

public interface MemberSupplier extends Supplier<SuppliedMember> {
	void init();

	void destroyState();
}
