package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.filebased.FileBasedMemberRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberRepository;
import ldes.client.treenodesupplier.repository.sql.postgres.PostgresEntityManagerFactory;
import ldes.client.treenodesupplier.repository.sql.sqlite.SqliteEntityManagerFactory;

import java.util.Map;

public class MemberRepositoryFactory {

	private MemberRepositoryFactory() {
	}

	public static MemberRepository getMemberRepository(StatePersistenceStrategy statePersistenceStrategy,
			Map<String, String> properties) {
		return switch (statePersistenceStrategy) {
			case SQLITE -> new SqlMemberRepository(SqliteEntityManagerFactory.getInstance());
			case MEMORY -> new InMemoryMemberRepository();
			case FILE -> new FileBasedMemberRepository();
			case POSTGRES -> new SqlMemberRepository(PostgresEntityManagerFactory.getInstance(properties));
		};
	}
}
