package ldes.client.treenodesupplier.filters;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.repository.MemberIdRepository;

/**
 * Filter that makes sure that a member is processed exactly only once
 */
public class ExactlyOnceFilter implements MemberFilter {
	private final MemberIdRepository memberIdRepository;
	private final boolean keepState;

	/**
	 * @param memberIdRepository repository to keep track if a member already has been processed
	 * @param keepState          if the state must be kept, or can be reset on restart of the filter
	 */
	public ExactlyOnceFilter(MemberIdRepository memberIdRepository, boolean keepState) {
		this.memberIdRepository = memberIdRepository;
		this.keepState = keepState;
	}

	@Override
	public boolean isAllowed(SuppliedMember member) {
		return !memberIdRepository.contains(member.getId());
	}

	@Override
	public void saveAllowedMember(SuppliedMember member) {
		memberIdRepository.addMemberId(member.getId());
	}

	/**
	 * Clean up the database when the filter is not required anymore and the state must not be kept
	 */
	@Override
	public void destroyState() {
		if (!keepState) {
			memberIdRepository.destroyState();
		}
	}
}
