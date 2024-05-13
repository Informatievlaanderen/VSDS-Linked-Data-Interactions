package ldes.client.treenodesupplier.membersuppliers;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.filters.MemberFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilteredMemberSupplier extends MemberSupplierDecorator {
	private static final Logger log = LoggerFactory.getLogger(FilteredMemberSupplier.class);
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
			log.debug("Member {} has been ignored by the {}", member.getId(), filter.getClass().getSimpleName());
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
