package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

import java.util.function.Supplier;

public interface MemberSupplier extends Supplier<SuppliedMember> {

	@Override
	SuppliedMember get();

	void destroyState();

}
