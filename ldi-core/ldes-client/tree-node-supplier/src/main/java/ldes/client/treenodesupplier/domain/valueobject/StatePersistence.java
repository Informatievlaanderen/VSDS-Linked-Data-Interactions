package ldes.client.treenodesupplier.domain.valueobject;

import ldes.client.treenodesupplier.domain.services.MemberRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordRepositoryFactory;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;

public class StatePersistence {

	private final MemberRepository memberRepository;
	private final TreeNodeRecordRepository treeNodeRecordRepository;

	public StatePersistence(MemberRepository memberRepository, TreeNodeRecordRepository treeNodeRecordRepository) {
		this.memberRepository = memberRepository;
		this.treeNodeRecordRepository = treeNodeRecordRepository;
	}

	public static StatePersistence from(StatePersistenceStrategy statePersistenceStrategy) {
		return new StatePersistence(MemberRepositoryFactory.getMemberRepository(statePersistenceStrategy),
				TreeNodeRecordRepositoryFactory
						.getTreeNodeRecordRepository(statePersistenceStrategy));
	}

	public MemberRepository getMemberRepository() {
		return memberRepository;
	}

	public TreeNodeRecordRepository getTreeNodeRecordRepository() {
		return treeNodeRecordRepository;
	}
}
