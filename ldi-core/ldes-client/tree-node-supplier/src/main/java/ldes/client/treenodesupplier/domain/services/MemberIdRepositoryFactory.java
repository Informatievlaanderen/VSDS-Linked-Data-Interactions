package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberIdRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberIdRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite.SqliteEntityManagerFactory;

import java.util.Map;

public class MemberIdRepositoryFactory {

	private MemberIdRepositoryFactory() {
	}

	public static MemberIdRepository getMemberIdRepository(StatePersistenceStrategy statePersistenceStrategy,
														   Map<String, String> properties, String instanceName) {
		return switch (statePersistenceStrategy) {
			case SQLITE -> new SqlMemberIdRepository(instanceName,
					SqliteEntityManagerFactory.getClientInstance(instanceName, properties));
			case MEMORY -> new InMemoryMemberIdRepository();
			case POSTGRES -> new SqlMemberIdRepository(instanceName,
					PostgresEntityManagerFactory.getClientInstance(instanceName, properties));
		};
	}
}
