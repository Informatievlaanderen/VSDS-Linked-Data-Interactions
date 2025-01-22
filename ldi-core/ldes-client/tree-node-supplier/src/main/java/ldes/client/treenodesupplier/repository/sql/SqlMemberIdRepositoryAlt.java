package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.StatelessQueryExecutor;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import org.hibernate.Session;
import org.hibernate.StatelessSession;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

public class SqlMemberIdRepositoryAlt implements MemberIdRepository {
	private final EntityManager entityManager;

	public SqlMemberIdRepositoryAlt(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Transactional
	@Override
	public boolean addMemberIdIfNotExists(String memberId) {
		return executeStatelessQuery(session -> session
				.createNamedQuery("MemberId.insert")
				.setParameter("memberId", memberId)
				.executeUpdate()) > 0;
	}

	@Override
	public void destroyState() {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
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
