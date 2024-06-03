package ldes.client.treenodesupplier.membersuppliers;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

/**
 * Decorator class implementation of the {@link MemberSupplier}
 */
public abstract class MemberSupplierDecorator implements MemberSupplier {
	private final MemberSupplier memberSupplier;

	protected MemberSupplierDecorator(MemberSupplier memberSupplier) {
		this.memberSupplier = memberSupplier;
	}

	/**
	 * Implementation of the decorator method that just is responsible for calling the provided base member supplier
	 * and return its result
	 *
	 * @return Result of the base member supplier
	 */
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
