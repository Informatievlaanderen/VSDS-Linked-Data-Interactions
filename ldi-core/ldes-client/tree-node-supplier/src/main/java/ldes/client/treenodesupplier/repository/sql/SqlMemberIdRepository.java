package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import ldes.client.treenodesupplier.repository.MemberIdRepository;

import javax.transaction.Transactional;

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
		return entityManagerFactory.executeStatelessQuery(session -> session
				.createNamedQuery("MemberId.insert")
				.setParameter("memberId", memberId)
				.executeUpdate()) > 0;
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}
}
