package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.domain.valueobject.StatePersistanceStrategy;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberRepository;
import ldes.client.treenodesupplier.repository.sqlite.SqliteMemberRepository;

public class MemberRepositoryFactory {

	private MemberRepositoryFactory() {
	}

	public static MemberRepository getMemberRepository(StatePersistanceStrategy statePersistanceStrategy) {
		return switch (statePersistanceStrategy) {
			case SQLITE -> new SqliteMemberRepository();
			case MEMORY -> new InMemoryMemberRepository();
		};
	}
}
