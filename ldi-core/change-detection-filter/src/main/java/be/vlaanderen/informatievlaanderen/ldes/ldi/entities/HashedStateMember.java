package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

public record HashedStateMember(String memberId, String memberHash) {

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof HashedStateMember that)) return false;

		return memberId.equals(that.memberId);
	}

	@Override
	public int hashCode() {
		return memberId.hashCode();
	}
}