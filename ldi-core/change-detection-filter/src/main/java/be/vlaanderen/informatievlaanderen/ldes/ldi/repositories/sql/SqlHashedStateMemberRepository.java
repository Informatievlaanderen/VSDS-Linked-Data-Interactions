package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.mapper.HashedStateMemberEntityMapper;
import jakarta.persistence.EntityManager;

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
				.createNamedQuery("HashedStateMember.findMember", HashedStateMemberEntity.class)
				.setParameter("memberId", hashedStateMember.memberId())
				.setParameter("memberHash", hashedStateMember.memberHash())
				.getResultStream()
				.findFirst()
				.isPresent();
	}

	@Override
	public void saveHashedStateMember(HashedStateMember hashedStateMember) {
		entityManager.getTransaction().begin();
		entityManager.merge(HashedStateMemberEntityMapper.fromHashedStateMember(hashedStateMember));
		entityManager.getTransaction().commit();
	}

	@Override
	public void destroyState() {
		entityManager.close();
		entityManagerFactory.destroyState(instanceName);
	}
}
