package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.h2.H2EntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberVersionRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberVersionRepository;

public class MemberVersionRepositoryFactory {

	private MemberVersionRepositoryFactory() {
	}

	/**
	 * @param statePersistenceStrategy via what persistence strategy the repository should work
	 * @param properties               a representation of the required config properties to set up the persistence unit
	 * @param instanceName             will be used to be able to more easily keep track of the return repo
	 * @return the memberVersionRepository for a specific instance (could be a NiFi flow or a LDIO pipeline)
	 */
	public static MemberVersionRepository getMemberVersionRepositoryFactory(StatePersistenceStrategy statePersistenceStrategy,
																			HibernateProperties properties, String instanceName) {
		return switch (statePersistenceStrategy) {
			case SQLITE ->
					new SqlMemberVersionRepository(SqliteEntityManagerFactory.getInstance(properties), instanceName);
			case MEMORY -> new InMemoryMemberVersionRepository();
			case H2 ->
					new SqlMemberVersionRepository(H2EntityManagerFactory.getInstance(instanceName, properties.getProperties()), instanceName);
			case POSTGRES ->
					new SqlMemberVersionRepository(PostgresEntityManagerFactory.getInstance(instanceName, properties.getProperties()), instanceName);
		};
	}
}
