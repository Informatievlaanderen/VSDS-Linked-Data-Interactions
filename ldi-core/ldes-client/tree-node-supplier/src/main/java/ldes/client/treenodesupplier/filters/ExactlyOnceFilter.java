package ldes.client.treenodesupplier.filters;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.repository.MemberIdRepository;

public class ExactlyOnceFilter implements MemberFilter {
	private final MemberIdRepository memberIdRepository;
	private final boolean keepState;

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

	@Override
	public void destroyState() {
		if(!keepState) {
			memberIdRepository.destroyState();
		}
	}
}
