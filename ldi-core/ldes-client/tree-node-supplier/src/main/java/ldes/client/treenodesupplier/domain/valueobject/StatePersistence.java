package ldes.client.treenodesupplier.domain.valueobject;

import ldes.client.treenodesupplier.domain.services.MemberIdRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.MemberRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordRepositoryFactory;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;

import java.util.Map;

public class StatePersistence {

	private final MemberRepository memberRepository;
	private final MemberIdRepository memberIdRepository;
	private final TreeNodeRecordRepository treeNodeRecordRepository;

	public StatePersistence(MemberRepository memberRepository, MemberIdRepository memberIdRepository, TreeNodeRecordRepository treeNodeRecordRepository) {
		this.memberRepository = memberRepository;
        this.memberIdRepository = memberIdRepository;
        this.treeNodeRecordRepository = treeNodeRecordRepository;
	}

	public static StatePersistence from(StatePersistenceStrategy statePersistenceStrategy,
			Map<String, String> properties, String instanceName) {
		return new StatePersistence(
				MemberRepositoryFactory.getMemberRepository(statePersistenceStrategy, properties, instanceName),
				MemberIdRepositoryFactory.getMemberIdRepository(statePersistenceStrategy, properties, instanceName),
				TreeNodeRecordRepositoryFactory
						.getTreeNodeRecordRepository(statePersistenceStrategy, properties, instanceName));
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
}
