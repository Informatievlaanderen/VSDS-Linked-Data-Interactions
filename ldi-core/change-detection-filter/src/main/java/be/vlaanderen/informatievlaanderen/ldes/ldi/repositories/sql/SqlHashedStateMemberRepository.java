package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;

import javax.transaction.Transactional;

public class SqlHashedStateMemberRepository implements HashedStateMemberRepository {
	private final EntityManagerFactory entityManagerFactory;
	private final String instanceName;

	public SqlHashedStateMemberRepository(EntityManagerFactory entityManagerFactory, String instanceName) {
		this.entityManagerFactory = entityManagerFactory;
		this.instanceName = instanceName;
	}

	@Transactional
	@Override
	public boolean saveHashedStateMemberIfNotExists(HashedStateMember hashedStateMember) {
		return entityManagerFactory.executeStatelessQuery(session -> session
				.createNamedQuery("HashedStateMember.insert")
				.setParameter(1, hashedStateMember.memberId())
				.setParameter(2, hashedStateMember.memberHash())
				.executeUpdate()) > 0;
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}
}
