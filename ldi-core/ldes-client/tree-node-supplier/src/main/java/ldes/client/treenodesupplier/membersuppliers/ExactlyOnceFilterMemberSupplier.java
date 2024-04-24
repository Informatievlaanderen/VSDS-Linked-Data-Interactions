package ldes.client.treenodesupplier.membersuppliers;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.filters.ExactlyOnceFilter;

public class ExactlyOnceFilterMemberSupplier extends MemberSupplierDecorator {
	private final ExactlyOnceFilter filter;

	public ExactlyOnceFilterMemberSupplier(MemberSupplier memberSupplier, ExactlyOnceFilter filter) {
		super(memberSupplier);
		this.filter = filter;
		Runtime.getRuntime().addShutdownHook(new Thread(this::destroyState));
	}

	@Override
	public SuppliedMember get() {
		SuppliedMember member = super.get();
		while (!filter.allowed(member.getId())) {
			member = super.get();
		}
		filter.addId(member.getId());
		return member;
	}

	@Override
	public void destroyState() {
		super.destroyState();
		filter.destroyState();
	}
}
