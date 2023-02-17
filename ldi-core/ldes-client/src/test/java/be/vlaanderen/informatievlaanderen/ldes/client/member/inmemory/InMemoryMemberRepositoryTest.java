package be.vlaanderen.informatievlaanderen.ldes.client.member.inmemory;

import be.vlaanderen.informatievlaanderen.ldes.client.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryMemberRepositoryTest {

	private MemberRepository memberRepository;

	@BeforeEach
	void setUp() {
		memberRepository = new InMemoryMemberRepository();
	}

	@Test
	void when_UnprocessedMemberIsAdded_ItIsProcessedAndCounterIncreases() {
		assertFalse(memberRepository.isProcessedMember("first"));
		assertEquals(0, memberRepository.countProcessedMembers());

		memberRepository.addProcessedMember("first");

		assertTrue(memberRepository.isProcessedMember("first"));
		assertEquals(1, memberRepository.countProcessedMembers());
	}

	@Test
	void when_ProcessedMemberIsAdded_ItRemainsProcessedAndCounterStaysTheSame() {
		memberRepository.addProcessedMember("first");
		assertTrue(memberRepository.isProcessedMember("first"));
		assertEquals(1, memberRepository.countProcessedMembers());

		memberRepository.addProcessedMember("first");

		assertTrue(memberRepository.isProcessedMember("first"));
		assertEquals(1, memberRepository.countProcessedMembers());
	}

	@Test
	void when_ProcessedMembersAreRemoved_NoMembersAreProcessedAndCounterIsZero() {
		memberRepository.addProcessedMember("first");
		memberRepository.addProcessedMember("second");
		assertTrue(memberRepository.isProcessedMember("first"));
		assertTrue(memberRepository.isProcessedMember("second"));
		assertEquals(2, memberRepository.countProcessedMembers());

		memberRepository.removeProcessedMembers();

		assertFalse(memberRepository.isProcessedMember("first"));
		assertEquals(0, memberRepository.countProcessedMembers());
	}

}