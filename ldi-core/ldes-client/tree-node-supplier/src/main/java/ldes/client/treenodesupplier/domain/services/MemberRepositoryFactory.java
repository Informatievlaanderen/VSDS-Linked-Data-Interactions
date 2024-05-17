package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite.SqliteEntityManagerFactory;

import java.util.Map;

public class MemberRepositoryFactory {

	private MemberRepositoryFactory() {
	}

	public static MemberRepository getMemberRepository(StatePersistenceStrategy statePersistenceStrategy,
			Map<String, String> properties, String instanceName) {
		return switch (statePersistenceStrategy) {
			case SQLITE -> new SqlMemberRepository(instanceName,
					SqliteEntityManagerFactory.getClientInstance(instanceName, properties));
			case MEMORY -> new InMemoryMemberRepository();
			case POSTGRES -> new SqlMemberRepository(instanceName,
					PostgresEntityManagerFactory.getClientInstance(instanceName, properties));
		};
	}
}
