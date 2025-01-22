package ldes.client.treenodesupplier.domain.valueobject;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.domain.services.MemberIdRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.MemberRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.MemberVersionRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordRepositoryFactory;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;

import javax.persistence.EntityManager;

public record LdesClientRepositories(MemberRepository memberRepository, MemberIdRepository memberIdRepository,
                                     TreeNodeRecordRepository treeNodeRecordRepository,
                                     MemberVersionRepository memberVersionRepository) {

	public static LdesClientRepositories from(StatePersistenceStrategy statePersistenceStrategy,
	                                          HibernateProperties properties, String instanceName) {
		return new LdesClientRepositories(
				MemberRepositoryFactory.getMemberRepository(statePersistenceStrategy, properties, instanceName),
				MemberIdRepositoryFactory.getMemberIdRepository(statePersistenceStrategy, properties, instanceName),
				TreeNodeRecordRepositoryFactory
						.getTreeNodeRecordRepository(statePersistenceStrategy, properties, instanceName),
				MemberVersionRepositoryFactory.getMemberVersionRepositoryFactory(statePersistenceStrategy, properties, instanceName));
	}

	public static LdesClientRepositories from(StatePersistenceStrategy statePersistenceStrategy, EntityManager entityManager) {
		return new LdesClientRepositories(
				MemberRepositoryFactory.getMemberRepository(statePersistenceStrategy, entityManager),
				MemberIdRepositoryFactory.getMemberIdRepository(statePersistenceStrategy, entityManager),
				TreeNodeRecordRepositoryFactory
						.getTreeNodeRecordRepository(statePersistenceStrategy, entityManager),
				MemberVersionRepositoryFactory.getMemberVersionRepositoryFactory(statePersistenceStrategy, entityManager));
	}
}
