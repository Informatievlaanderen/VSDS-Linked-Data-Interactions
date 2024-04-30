package ldes.client.treenodesupplier.domain.valueobject;

import ldes.client.treenodesupplier.domain.services.MemberIdRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.MemberRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.MemberVersionRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordRepositoryFactory;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;

import java.util.Map;

public class StatePersistence {

    private final MemberRepository memberRepository;
    private final MemberIdRepository memberIdRepository;
    private final TreeNodeRecordRepository treeNodeRecordRepository;
    private final MemberVersionRepository memberVersionRepository;

    public StatePersistence(MemberRepository memberRepository, MemberIdRepository memberIdRepository, TreeNodeRecordRepository treeNodeRecordRepository, MemberVersionRepository memberVersionRepository) {
        this.memberRepository = memberRepository;
        this.memberIdRepository = memberIdRepository;
        this.treeNodeRecordRepository = treeNodeRecordRepository;
        this.memberVersionRepository = memberVersionRepository;
    }

    public static StatePersistence from(StatePersistenceStrategy statePersistenceStrategy,
                                        Map<String, String> properties, String instanceName) {
        return new StatePersistence(
                MemberRepositoryFactory.getMemberRepository(statePersistenceStrategy, properties, instanceName),
                MemberIdRepositoryFactory.getMemberIdRepository(statePersistenceStrategy, properties, instanceName),
                TreeNodeRecordRepositoryFactory
                        .getTreeNodeRecordRepository(statePersistenceStrategy, properties, instanceName),
                MemberVersionRepositoryFactory.getMemberVersionRepositoryFactory(statePersistenceStrategy, properties, instanceName));
    }

    public MemberRepository getMemberRepository() {
        return memberRepository;
    }

    public MemberIdRepository getMemberIdRepository() {
        return memberIdRepository;
    }

    public TreeNodeRecordRepository getTreeNodeRecordRepository() {
        return treeNodeRecordRepository;
    }

	public MemberVersionRepository getMemberVersionRepository() {
		return memberVersionRepository;
	}
}
