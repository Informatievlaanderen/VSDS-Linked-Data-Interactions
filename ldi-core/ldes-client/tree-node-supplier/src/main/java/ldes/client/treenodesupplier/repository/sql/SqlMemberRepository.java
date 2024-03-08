package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import ldes.client.treenodesupplier.repository.MemberRepository;

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
				.createNamedQuery("Member.getFirstByMemberStatus", MemberRecordEntity.class)
				.setParameter("memberStatus", MemberStatus.UNPROCESSED)
				.getResultStream()
				.map(MemberRecordEntity::toMemberRecord)
				.findFirst();

	}

	@Override
	public void deleteMember(MemberRecord member) {
		entityManager.getTransaction().begin();
		entityManager
				.createNamedQuery("Member.deleteByMemberId")
				.setParameter("id", member.getMemberId())
				.executeUpdate();
		entityManager.getTransaction().commit();
	}

	@Override
	public void saveTreeMembers(Stream<MemberRecord> treeMemberStream) {
		entityManager.getTransaction().begin();
		treeMemberStream.map(MemberRecordEntity::fromMemberRecord)
				.forEach(entityManager::merge);
		entityManager.getTransaction().commit();
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}
}
