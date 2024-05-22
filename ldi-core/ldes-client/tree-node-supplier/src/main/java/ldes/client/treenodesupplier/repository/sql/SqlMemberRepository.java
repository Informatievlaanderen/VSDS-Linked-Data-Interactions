package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.MemberRecordEntity;
import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.mapper.MemberRecordEntityMapper;

import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

public class SqlMemberRepository implements MemberRepository {
	final EntityManagerFactory entityManagerFactory;
	private final EntityManager entityManager;
	private final String instanceName;

	public SqlMemberRepository(String instanceName, EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		this.entityManager = entityManagerFactory.getEntityManager();
		this.instanceName = instanceName;
	}

	@Override
	public Optional<MemberRecord> getTreeMember() {

		return entityManager
				.createNamedQuery("Member.getFirst", MemberRecordEntity.class)
				.getResultStream()
				.map(MemberRecordEntityMapper::toMemberRecord)
				.findFirst();

	}

	@Override
	public void deleteMember(MemberRecord member) {
		entityManager.getTransaction().begin();
		entityManager
				.createNamedQuery("Member.deleteByMemberId")
				.setParameter("memberId", member.getMemberId())
				.executeUpdate();
		entityManager.getTransaction().commit();
	}

	@Override
	public void saveTreeMembers(Stream<MemberRecord> treeMemberStream) {
		entityManager.getTransaction().begin();
		treeMemberStream.map(MemberRecordEntityMapper::fromMemberRecord)
				.forEach(entityManager::merge);
		entityManager.getTransaction().commit();
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}
}
