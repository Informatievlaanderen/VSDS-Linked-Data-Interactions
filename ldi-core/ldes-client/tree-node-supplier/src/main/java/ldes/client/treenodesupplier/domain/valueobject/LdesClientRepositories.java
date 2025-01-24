package ldes.client.treenodesupplier.domain.valueobject;

import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberIdRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberVersionRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberIdRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberVersionRepository;
import ldes.client.treenodesupplier.repository.sql.SqlTreeNodeRepository;

import javax.persistence.EntityManager;

public record LdesClientRepositories(MemberRepository memberRepository, MemberIdRepository memberIdRepository,
                                     TreeNodeRecordRepository treeNodeRecordRepository,
                                     MemberVersionRepository memberVersionRepository) {

	public static LdesClientRepositories sqlBased(EntityManager entityManager) {
		return new LdesClientRepositories(
				new SqlMemberRepository(entityManager),
				new SqlMemberIdRepository(entityManager),
				new SqlTreeNodeRepository(entityManager),
				new SqlMemberVersionRepository(entityManager));
	}

	public static LdesClientRepositories memoryBased() {
		return new LdesClientRepositories(
				new InMemoryMemberRepository(),
				new InMemoryMemberIdRepository(),
				new InMemoryTreeNodeRecordRepository(),
				new InMemoryMemberVersionRepository());
	}
}
