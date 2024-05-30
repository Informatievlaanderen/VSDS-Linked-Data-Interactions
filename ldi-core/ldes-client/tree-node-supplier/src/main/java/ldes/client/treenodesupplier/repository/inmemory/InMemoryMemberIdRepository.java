package ldes.client.treenodesupplier.repository.inmemory;

import ldes.client.treenodesupplier.repository.MemberIdRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryMemberIdRepository implements MemberIdRepository {
	private final List<String> memberIds = new ArrayList<>();

	@Override
	public void addMemberId(String memberId) {
		memberIds.add(memberId);
	}

	@Override
	public boolean contains(String memberId) {
		return memberIds.contains(memberId);
	}

	@Override
	public void destroyState() {
		memberIds.clear();
	}
}
