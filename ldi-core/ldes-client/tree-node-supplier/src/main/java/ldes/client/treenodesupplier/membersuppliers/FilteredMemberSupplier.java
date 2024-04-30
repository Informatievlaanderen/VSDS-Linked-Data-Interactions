package ldes.client.treenodesupplier.membersuppliers;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.filters.MemberFilter;

public class FilteredMemberSupplier extends MemberSupplierDecorator {
	private final MemberFilter filter;

	public FilteredMemberSupplier(MemberSupplier memberSupplier, MemberFilter filter) {
		super(memberSupplier);
		this.filter = filter;
		Runtime.getRuntime().addShutdownHook(new Thread(this::destroyState));
	}

	@Override
	public SuppliedMember get() {
		SuppliedMember member = super.get();
		while (!filter.isAllowed(member)) {
			member = super.get();
		}
		filter.saveAllowedMember(member);
		return member;
	}

	@Override
	public void destroyState() {
		super.destroyState();
		filter.destroyState();
	}
}
