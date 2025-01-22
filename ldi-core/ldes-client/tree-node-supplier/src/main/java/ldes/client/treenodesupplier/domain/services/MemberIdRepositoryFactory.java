package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberIdRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberIdRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberIdRepositoryAlt;

import javax.persistence.EntityManager;

public class MemberIdRepositoryFactory {

	private MemberIdRepositoryFactory() {
	}

	/**
	 * @param statePersistenceStrategy via what persistence strategy the repository should work
	 * @param properties               a representation of the required config properties to set up the persistence unit
	 * @param instanceName             will be used to be able to more easily keep track of the return repo
	 * @return the memberIdRepository for a specific instance (could be a NiFi flow or a LDIO pipeline)
	 */
	public static MemberIdRepository getMemberIdRepository(StatePersistenceStrategy statePersistenceStrategy,
														   HibernateProperties properties, String instanceName) {
		return switch (statePersistenceStrategy) {
			case SQLITE -> new SqlMemberIdRepository(instanceName,
					SqliteEntityManagerFactory.getInstance(properties));
			case MEMORY -> new InMemoryMemberIdRepository();
			case POSTGRES -> new SqlMemberIdRepository(instanceName,
					PostgresEntityManagerFactory.getInstance(instanceName, properties.getProperties()));
		};
	}

	public static MemberIdRepository getMemberIdRepository(StatePersistenceStrategy statePersistenceStrategy, EntityManager entityManager) {
		return switch (statePersistenceStrategy) {
			case SQLITE, POSTGRES -> new SqlMemberIdRepositoryAlt(entityManager);
			case MEMORY -> new InMemoryMemberIdRepository();
		};
	}
}
