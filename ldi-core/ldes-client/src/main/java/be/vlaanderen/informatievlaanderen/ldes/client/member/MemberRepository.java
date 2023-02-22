package be.vlaanderen.informatievlaanderen.ldes.client.member;

public interface MemberRepository {
	boolean isProcessedMember(String memberId);

	void removeProcessedMembers();

	long countProcessedMembers();

	void addProcessedMember(String memberId);

	void clearState();
}
