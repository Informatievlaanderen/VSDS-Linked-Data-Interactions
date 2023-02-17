package be.vlaanderen.informatievlaanderen.ldes.client.member.inmemory;

import be.vlaanderen.informatievlaanderen.ldes.client.member.MemberRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryMemberRepository implements MemberRepository {

	private final List<String> processedMembers;

	public InMemoryMemberRepository() {
		this.processedMembers = new ArrayList<>();
	}

	@Override
	public boolean isProcessedMember(String memberId) {
		return processedMembers.contains(memberId);
	}

	@Override
	public void removeProcessedMembers() {
		processedMembers.clear();
	}

	@Override
	public long countProcessedMembers() {
		return processedMembers.size();
	}

	@Override
	public void addProcessedMember(String memberId) {
		if (!isProcessedMember(memberId)) {
			processedMembers.add(memberId);
		}
	}

	@Override
	public void clearState() {
		removeProcessedMembers();
	}
}
