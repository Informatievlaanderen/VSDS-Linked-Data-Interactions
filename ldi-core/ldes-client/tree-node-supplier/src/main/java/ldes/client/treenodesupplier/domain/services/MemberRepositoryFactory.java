package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.filebased.FileBasedMemberRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberRepository;
import ldes.client.treenodesupplier.repository.sqlite.SqliteMemberRepository;

public class MemberRepositoryFactory {

	private MemberRepositoryFactory() {
	}

	public static MemberRepository getMemberRepository(StatePersistenceStrategy statePersistenceStrategy) {
		return switch (statePersistenceStrategy) {
			case SQLITE -> new SqliteMemberRepository();
			case MEMORY -> new InMemoryMemberRepository();
			case FILE -> new FileBasedMemberRepository();
		};
	}
}
