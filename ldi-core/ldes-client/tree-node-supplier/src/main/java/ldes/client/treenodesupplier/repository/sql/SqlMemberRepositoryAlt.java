package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.StatelessQueryExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.MemberRecordEntity;
import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.mapper.MemberRecordEntityMapper;
import org.hibernate.Session;
import org.hibernate.StatelessSession;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.stream.Stream;

public class SqlMemberRepositoryAlt implements MemberRepository {
	private final EntityManager entityManager;

	public SqlMemberRepositoryAlt(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Optional<MemberRecord> getTreeMember() {
		return entityManager
				.createNamedQuery("Member.getAllOrderedByCreation", MemberRecordEntity.class)
				.setMaxResults(1)
				.getResultStream()
				.findFirst()
				.map(MemberRecordEntityMapper::toMemberRecord);
	}

	@Override
	public void deleteMember(MemberRecord member) {
		executeStatelessQuery(session -> session
				.createNamedQuery("Member.deleteByMemberId")
				.setParameter("memberId", member.getMemberId())
				.executeUpdate());
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
		entityManager.clear();
	}

	private int executeStatelessQuery(StatelessQueryExecutor queryExecutor) {
		final Session session = entityManager.unwrap(Session.class);
		return session.doReturningWork(connection -> {
			try (final StatelessSession statelessSession = session.getSessionFactory().openStatelessSession(connection)) {
				return queryExecutor.execute(statelessSession);
			}
		});
	}
}
