package ldes.client.treenodesupplier.repository.inmemory;

import ldes.client.treenodesupplier.repository.MemberIdRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryMemberIdRepository implements MemberIdRepository {
	private final List<String> memberIds = new ArrayList<>();

	@Override
	public boolean addMemberIdIfNotExists(String memberId) {
		if (memberIds.contains(memberId)) {
			return false;
		}
		memberIds.add(memberId);
		return true;
	}

	@Override
	public void destroyState() {
		memberIds.clear();
	}
}
