package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import jakarta.transaction.Transactional;
import ldes.client.treenodesupplier.repository.MemberIdRepository;

public class SqlMemberIdRepository implements MemberIdRepository {
	private final EntityManagerFactory entityManagerFactory;
	private final String instanceName;

	public SqlMemberIdRepository(String instanceName, EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		this.instanceName = instanceName;
	}

	@Transactional
	@Override
	public boolean addMemberIdIfNotExists(String memberId) {
		var transaction = entityManagerFactory.getEntityManager().getTransaction();
		transaction.begin();
		var success = entityManagerFactory.getEntityManager()
				.createNamedQuery("MemberId.insert")
				.setParameter("memberId", memberId)
				              .executeUpdate() > 0;
		transaction.commit();
		return success;
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}
}
