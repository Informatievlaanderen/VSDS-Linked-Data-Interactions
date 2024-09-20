package be.vlaanderen.informatievlaanderen.ldes.ldi;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.StatelessSession;

public abstract class AbstractEntityManagerFactory implements EntityManagerFactory {
	private final EntityManager entityManager;

	protected AbstractEntityManagerFactory(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public int executeStatelessQuery(StatelessQueryExecutor queryExecutor) {
		final Session session = entityManager.unwrap(Session.class);
		return session.doReturningWork(connection -> {
			try (final StatelessSession statelessSession = session.getSessionFactory().openStatelessSession(connection)) {
				return queryExecutor.execute(statelessSession);
			}
		});
	}

	@Override
	public void destroyState(String instanceName) {
		entityManager.close();
	}
}
