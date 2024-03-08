package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.repository.MemberIdRepository;

public class ExacltyOnceFilter {
    private final MemberIdRepository memberIdRepository;

    public ExacltyOnceFilter(MemberIdRepository memberIdRepository) {
        this.memberIdRepository = memberIdRepository;
    }

    public boolean checkFilter(String memberId) {
        return !memberIdRepository.contains(memberId);
    }
    public void addId(String memberId) {
        memberIdRepository.addMemberId(memberId);
    }

    public void destroyState() {
        memberIdRepository.destroyState();
    }
}
