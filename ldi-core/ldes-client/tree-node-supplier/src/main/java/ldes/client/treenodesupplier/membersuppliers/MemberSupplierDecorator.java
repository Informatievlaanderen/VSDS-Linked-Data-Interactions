package ldes.client.treenodesupplier.membersuppliers;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

public abstract class MemberSupplierDecorator implements MemberSupplier {
	private final MemberSupplier memberSupplier;

	protected MemberSupplierDecorator(MemberSupplier memberSupplier) {
		this.memberSupplier = memberSupplier;
	}


	@Override
	public SuppliedMember get() {
		return memberSupplier.get();
	}

	@Override
	public void init() {
		memberSupplier.init();
	}

	@Override
	public void destroyState() {
		memberSupplier.destroyState();
	}
}
