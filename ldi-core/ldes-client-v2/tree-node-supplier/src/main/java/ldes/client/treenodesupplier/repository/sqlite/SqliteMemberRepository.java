package ldes.client.treenodesupplier.repository.sqlite;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import ldes.client.treenodesupplier.repository.MemberRepository;

import javax.persistence.EntityManager;
import java.util.Optional;

public class SqliteMemberRepository implements MemberRepository {
	private final EntityManagerFactory entityManagerFactory = EntityManagerFactory.getInstance();
	private final EntityManager entityManager = entityManagerFactory.getEntityManager();

	@Override
	public Optional<MemberRecord> getUnprocessedTreeMember() {

		return entityManager
				.createNamedQuery("Member.getByMemberStatus")
				.setParameter("memberStatus", MemberStatus.UNPROCESSED)
				.getResultStream()
				.map(x -> ((MemberRecordEntity) x).toMemberRecord())
				.findFirst();

	}

	@Override
	public boolean isProcessed(MemberRecord member) {
		return ((Number) entityManager
				.createNamedQuery("Member.isProcessed")
				.setParameter("memberStatus", MemberStatus.PROCESSED)
				.setParameter("id", member.getMemberId())
				.getSingleResult()).longValue() > 0;
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
