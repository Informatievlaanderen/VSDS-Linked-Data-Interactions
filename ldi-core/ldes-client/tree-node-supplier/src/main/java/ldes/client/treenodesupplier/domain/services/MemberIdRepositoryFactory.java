package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberIdRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberIdRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberRepository;
import ldes.client.treenodesupplier.repository.sql.postgres.PostgresEntityManagerFactory;
import ldes.client.treenodesupplier.repository.sql.sqlite.SqliteEntityManagerFactory;

import java.util.Map;

public class MemberIdRepositoryFactory {

	private MemberIdRepositoryFactory() {
	}

	public static MemberIdRepository getMemberRepository(StatePersistenceStrategy statePersistenceStrategy,
														 Map<String, String> properties, String instanceName) {
		return switch (statePersistenceStrategy) {
			case SQLITE -> new SqlMemberIdRepository(instanceName,
					SqliteEntityManagerFactory.getInstance(instanceName));
			case MEMORY -> new InMemoryMemberIdRepository();
			case POSTGRES -> new SqlMemberIdRepository(instanceName,
					PostgresEntityManagerFactory.getInstance(instanceName, properties));
		};
	}
}
