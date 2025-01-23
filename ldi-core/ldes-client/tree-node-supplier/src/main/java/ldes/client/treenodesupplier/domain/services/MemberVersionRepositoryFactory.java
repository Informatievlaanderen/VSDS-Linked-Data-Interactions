package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberVersionRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberVersionRepository;

import javax.persistence.EntityManager;

public class MemberVersionRepositoryFactory {

	private MemberVersionRepositoryFactory() {
	}

	/**
	 * @param statePersistenceStrategy via what persistence strategy the repository should work
	 * @param entityManager a persistence unit to manage the entities
	 * @return the memberVersionRepository for a specific instance (could be a NiFi flow or a LDIO pipeline)
	 */
	public static MemberVersionRepository getMemberVersionRepositoryFactory(StatePersistenceStrategy statePersistenceStrategy, EntityManager entityManager) {
		return switch (statePersistenceStrategy) {
			case SQLITE, POSTGRES -> new SqlMemberVersionRepository(entityManager);
			case MEMORY -> new InMemoryMemberVersionRepository();
		};
	}
}
