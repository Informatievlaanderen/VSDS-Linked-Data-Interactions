package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql.entities.HashedStateMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.EntityManagerFactory;

import javax.persistence.EntityManager;

public class SqlHashedStateMemberRepository implements HashedStateMemberRepository {
	private final EntityManagerFactory entityManagerFactory;
	private final EntityManager entityManager;
	private final String instanceName;

	public SqlHashedStateMemberRepository(EntityManagerFactory entityManagerFactory, String instanceName) {
		this.entityManagerFactory = entityManagerFactory;
		this.entityManager = entityManagerFactory.getEntityManager();
		this.instanceName = instanceName;
	}

	@Override
	public boolean containsHashedStateMember(HashedStateMember hashedStateMember) {
		return entityManager
				.createNamedQuery("HashedStateMember.containsMember", Boolean.class)
				.setParameter("memberId", hashedStateMember.memberId())
				.setParameter("memberHash", hashedStateMember.memberHash())
				.getSingleResult();
	}

	@Override
	public void saveHashedStateMember(HashedStateMember hashedStateMember) {
		entityManager.merge(HashedStateMemberEntity.fromHashedStateMember(hashedStateMember));
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}
}
