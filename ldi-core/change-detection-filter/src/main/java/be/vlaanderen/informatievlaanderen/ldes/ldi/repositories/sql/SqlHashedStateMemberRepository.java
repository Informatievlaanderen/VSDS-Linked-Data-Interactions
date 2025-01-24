package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.mapper.HashedStateMemberEntityMapper;

import javax.persistence.EntityManager;

public class SqlHashedStateMemberRepository implements HashedStateMemberRepository {
	private final EntityManager entityManager;

	public SqlHashedStateMemberRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
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
	public void close() {
		entityManager.close();
	}
}
