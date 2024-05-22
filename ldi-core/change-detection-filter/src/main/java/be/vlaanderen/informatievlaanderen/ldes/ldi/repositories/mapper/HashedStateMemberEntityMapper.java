package be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.mapper;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMemberEntity;

public class HashedStateMemberEntityMapper {
	private HashedStateMemberEntityMapper() {
	}

	public static HashedStateMemberEntity fromHashedStateMember(HashedStateMember hashedStateMember) {
		return new HashedStateMemberEntity(hashedStateMember.memberId(), hashedStateMember.memberHash());
	}
}
