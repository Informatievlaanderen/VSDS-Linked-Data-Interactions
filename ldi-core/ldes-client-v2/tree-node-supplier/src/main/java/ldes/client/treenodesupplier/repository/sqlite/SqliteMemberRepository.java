package ldes.client.treenodesupplier.repository.sqlite;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import ldes.client.treenodesupplier.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SqliteMemberRepository implements MemberRepository {
	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pu-sqlite-jpa");

	private final EntityManager entityManager = entityManagerFactory.createEntityManager();

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
		try {
			entityManager.close();
			entityManagerFactory.close();
			Files.delete(Path.of("member-database.db"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
