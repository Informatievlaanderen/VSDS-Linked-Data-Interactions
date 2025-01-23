package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberIdRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberIdRepository;

import javax.persistence.EntityManager;

public class MemberIdRepositoryFactory {

	private MemberIdRepositoryFactory() {
	}

	/**
	 * @param statePersistenceStrategy via what persistence strategy the repository should work
	 * @param entityManager a persistence unit to manage the entities
	 * @return the memberIdRepository for a specific instance (could be a NiFi flow or a LDIO pipeline)
	 */
	public static MemberIdRepository getMemberIdRepository(StatePersistenceStrategy statePersistenceStrategy, EntityManager entityManager) {
		return switch (statePersistenceStrategy) {
			case SQLITE, POSTGRES -> new SqlMemberIdRepository(entityManager);
			case MEMORY -> new InMemoryMemberIdRepository();
		};
	}
}
