package ldes.client.treenodesupplier.repository;

public interface MemberIdRepository {
    void addMemberId(String memberId);
    boolean contains(String memberId);
    void destroyState();
}
