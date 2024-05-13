package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.inmemory;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;

import java.util.*;

public class InMemoryHashedStateMemberRepository implements HashedStateMemberRepository {
	private final Map<String, HashedStateMember> members;

	public InMemoryHashedStateMemberRepository() {
		members = new HashMap<>();
	}


	@Override
	public Optional<HashedStateMember> getHashedStateMember(String id) {
		return Optional.ofNullable(members.get(id));
	}

	@Override
	public boolean containsHashedStateMember(HashedStateMember hashedStateMember) {
		final HashedStateMember member = members.get(hashedStateMember.memberId());
		return member != null && member.memberHash().equals(hashedStateMember.memberHash());
	}

	@Override
	public void saveHashedStateMember(HashedStateMember hashedStateMember) {
		members.put(hashedStateMember.memberId(), hashedStateMember);
	}

	@Override
	public void destroyState() {
		members.clear();
	}
}
