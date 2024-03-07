package ldes.client.treenodesupplier.repository;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;

public interface MemberIdRepository {
    void addMemberId(String memberId);
    boolean contains(String memberId);
}
