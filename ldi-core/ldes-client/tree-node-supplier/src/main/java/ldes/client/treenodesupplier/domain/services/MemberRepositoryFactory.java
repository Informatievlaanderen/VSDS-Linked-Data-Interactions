package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberRepository;

import javax.persistence.EntityManager;

public class MemberRepositoryFactory {

	private MemberRepositoryFactory() {
	}

	/**
	 * @param statePersistenceStrategy via what persistence strategy the repository should work
	 * @param entityManager a persistence unit to manage the entities
	 * @return the memberRepository for a specific instance (could be a NiFi flow or a LDIO pipeline)
	 */
	public static MemberRepository getMemberRepository(StatePersistenceStrategy statePersistenceStrategy, EntityManager entityManager) {
		return switch (statePersistenceStrategy) {
			case SQLITE, POSTGRES -> new SqlMemberRepository(entityManager);
			case MEMORY -> new InMemoryMemberRepository();
		};
	}
}
