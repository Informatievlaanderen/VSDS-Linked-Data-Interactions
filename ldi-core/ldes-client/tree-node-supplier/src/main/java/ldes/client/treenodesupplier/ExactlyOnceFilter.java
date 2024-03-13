package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.repository.MemberIdRepository;

public class ExactlyOnceFilter {
    private final MemberIdRepository memberIdRepository;

    public ExactlyOnceFilter(MemberIdRepository memberIdRepository) {
        this.memberIdRepository = memberIdRepository;
    }

    public boolean allowed(String memberId) {
        return !memberIdRepository.contains(memberId);
    }
    public void addId(String memberId) {
        memberIdRepository.addMemberId(memberId);
    }

    public void destroyState() {
        memberIdRepository.destroyState();
    }
}
