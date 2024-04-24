package ldes.client.treenodesupplier.filters;

import ldes.client.treenodesupplier.repository.MemberIdRepository;

public class ExactlyOnceFilter {
	private final MemberIdRepository memberIdRepository;
	private final boolean keepState;

	public ExactlyOnceFilter(MemberIdRepository memberIdRepository, boolean keepState) {
		this.memberIdRepository = memberIdRepository;
		this.keepState = keepState;
	}

	public boolean allowed(String memberId) {
		return !memberIdRepository.contains(memberId);
	}

	public void addId(String memberId) {
		memberIdRepository.addMemberId(memberId);
	}

	public void destroyState() {
		if(keepState) {
			memberIdRepository.destroyState();
		}
	}
}
