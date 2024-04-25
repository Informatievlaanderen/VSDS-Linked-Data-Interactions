package ldes.client.treenodesupplier.repository.inmemory;

import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryMemberVersionRepository implements MemberVersionRepository {
    private final Map<String, MemberVersionRecord> memberVersionRecords = new HashMap<>();

    @Override
    public void addMemberVersion(MemberVersionRecord memberVersion) {
        memberVersionRecords.put(memberVersion.getVersionOf(), memberVersion);
    }

    @Override
    public boolean isVersionAfterTimestamp(MemberVersionRecord memberVersion) {
        return Optional.ofNullable(memberVersionRecords.get(memberVersion.getVersionOf()))
                .filter(presentMember -> !presentMember.getTimestamp().isAfter(memberVersion.getTimestamp()))
                .isPresent();
    }

    @Override
    public void destroyState() {
        memberVersionRecords.clear();
    }

}
