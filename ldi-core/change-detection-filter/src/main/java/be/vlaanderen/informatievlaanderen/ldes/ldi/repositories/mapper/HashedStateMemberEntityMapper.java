package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.mapper;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMemberEntity;

public class HashedStateMemberEntityMapper {
	private HashedStateMemberEntityMapper() {
	}

	/**
	 * Maps a HashedStateMember from a domain object to an entity object
	 *
	 * @param hashedStateMember the domain object
	 * @return the entity object
	 */
	public static HashedStateMemberEntity fromHashedStateMember(HashedStateMember hashedStateMember) {
		return new HashedStateMemberEntity(hashedStateMember.memberId(), hashedStateMember.memberHash());
	}
}
