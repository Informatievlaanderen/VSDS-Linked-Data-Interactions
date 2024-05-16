package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;

public interface HashedStateMemberRepository {
	boolean containsHashedStateMember(HashedStateMember hashedStateMember);

	void saveHashedStateMember(HashedStateMember hashedStateMember);

	void destroyState();
}