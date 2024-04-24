package ldes.client.treenodesupplier.repository;

import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;

public interface MemberVersionRepository {

    void addMemberVersion(MemberVersionRecord memberVersion);

    boolean isVersionAfterTimestamp(MemberVersionRecord memberVersion);

    void destroyState();

}
