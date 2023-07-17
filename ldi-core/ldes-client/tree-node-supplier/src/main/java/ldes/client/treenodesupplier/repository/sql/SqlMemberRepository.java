package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import ldes.client.treenodesupplier.repository.MemberRepository;

import java.util.Optional;

import javax.persistence.EntityManager;

public class SqlMemberRepository implements MemberRepository {
	private final EntityManagerFactory entityManagerFactory;
	private final EntityManager entityManager;

	public SqlMemberRepository(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		this.entityManager = entityManagerFactory.getEntityManager();
	}

	@Override
	public Optional<MemberRecord> getUnprocessedTreeMember() {

		return entityManager
				.createNamedQuery("Member.getByMemberStatus", MemberRecordEntity.class)
				.setParameter("memberStatus", MemberStatus.UNPROCESSED)
				.getResultStream()
				.map(MemberRecordEntity::toMemberRecord)
				.findFirst();

	}

	@Override
	public boolean isProcessed(MemberRecord member) {
		return entityManager
				.createNamedQuery("Member.countByMemberStatusAndId", Long.class)
				.setParameter("memberStatus", MemberStatus.PROCESSED)
				.setParameter("id", member.getMemberId())
				.getSingleResult() > 0;
	}

	@Override
	public void saveTreeMember(MemberRecord treeMember) {
		MemberRecordEntity memberRecordEntity = MemberRecordEntity.fromMemberRecord(treeMember);
		entityManager.getTransaction().begin();
		entityManager.merge(memberRecordEntity);
		entityManager.getTransaction().commit();
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState();
	}
}
