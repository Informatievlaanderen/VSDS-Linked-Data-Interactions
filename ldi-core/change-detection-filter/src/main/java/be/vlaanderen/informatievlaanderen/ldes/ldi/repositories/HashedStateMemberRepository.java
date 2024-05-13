package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;

import java.util.Optional;

public interface HashedStateMemberRepository {
	Optional<HashedStateMember> getHashedStateMember(String id);

	boolean containsHashedStateMember(HashedStateMember hashedStateMember);

	void saveHashedStateMember(HashedStateMember hashedStateMember);

	void destroyState();
}
