package ldes.client.treenodesupplier.membersuppliers;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.filters.MemberFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a decorator for the {@link MemberSupplier} which makes it possible filter out some members before
 * supplying them
 */
public class FilteredMemberSupplier extends MemberSupplierDecorator {
	private static final Logger log = LoggerFactory.getLogger(FilteredMemberSupplier.class);
	private final MemberFilter filter;

	public FilteredMemberSupplier(MemberSupplier memberSupplier, MemberFilter filter) {
		super(memberSupplier);
		this.filter = filter;
		Runtime.getRuntime().addShutdownHook(new Thread(this::destroyState));
	}

	/**
	 * Extended method that will return the first member that gets through the provided filter. If it gets through it,
	 * then the member will first be saved before it is returned
	 *
	 * @return the first member that gets through the filter
	 */
	@Override
	public SuppliedMember get() {
		SuppliedMember member = super.get();
		while (!filter.saveMemberIfAllowed(member)) {
			log.debug("Member {} has been ignored by the {}", member.getId(), filter.getClass().getSimpleName());
			member = super.get();
		}
		return member;
	}

	@Override
	public void destroyState() {
		super.destroyState();
		filter.destroyState();
	}
}
