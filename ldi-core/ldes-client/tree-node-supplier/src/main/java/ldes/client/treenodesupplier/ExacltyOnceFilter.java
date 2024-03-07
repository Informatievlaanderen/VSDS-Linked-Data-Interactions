package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;

public class ExacltyOnceFilter {
    private final MemberIdRepository memberIdRepository;
    private final boolean useFilter;

    public ExacltyOnceFilter(MemberIdRepository memberIdRepository, boolean useFilter) {
        this.memberIdRepository = memberIdRepository;
        this.useFilter = useFilter;
    }

    public boolean checkFilter(String memberId) {
        return !memberIdRepository.contains(memberId) && useFilter;
    }
    public void addId(String memberId) {
        memberIdRepository.addMemberId(memberId);
    }
}
