package ldes.client.treenodesupplier.repository.inmemory;

import ldes.client.treenodesupplier.repository.MemberIdRepository;

import java.util.List;

public class InMemoryMemberIdRepository implements MemberIdRepository {
    private List<String> memberIds;
    @Override
    public void addMemberId(String memberId) {
        memberIds.add(memberId);
    }

    @Override
    public boolean contains(String memberId) {
        return memberIds.contains(memberId);
    }
}
