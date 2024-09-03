package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.hibernate.Session;
import org.hibernate.StatelessSession;

import javax.persistence.EntityManager;

public abstract class AbstractEntityManagerFactory implements EntityManagerFactory {
	private final javax.persistence.EntityManagerFactory entityManagerFactory;
	private final EntityManager entityManager;

	protected AbstractEntityManagerFactory(javax.persistence.EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		this.entityManager = entityManagerFactory.createEntityManager();
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
		entityManagerFactory.close();
	}
}
