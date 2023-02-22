package be.vlaanderen.informatievlaanderen.ldes.client.state;

import be.vlaanderen.informatievlaanderen.ldes.client.member.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.client.member.inmemory.InMemoryMemberRepository;

import java.time.LocalDateTime;
import java.util.*;

/**
 * An implementation of the {@link LdesStateManager} that does not persist
 * state.
 *
 * This implementation is not capable of pausing or resuming an LDES.
 */
public class NonPersistedLdesStateManager extends LdesStateManager {

	protected final Queue<String> fragmentsToProcess;
	protected final List<String> processedImmutableFragments;
	/**
	 * A map of key-value pairs with the fragment id as key.
	 */
	protected final Map<String, LocalDateTime> processedMutableFragments;
	private final MemberRepository memberRepository;

	public NonPersistedLdesStateManager() {
		super();

		fragmentsToProcess = new ArrayDeque<>();

		processedImmutableFragments = new ArrayList<>();
		processedMutableFragments = new HashMap<>();
		memberRepository = new InMemoryMemberRepository();
	}

	@Override
	public boolean destroyState() {
		// not applicable for non persisted states
		return true;
	}

	@Override
	public boolean hasQueuedFragments() {
		return !fragmentsToProcess.isEmpty();
	}

	@Override
	public boolean isQueuedFragment(String fragmentId) {
		return fragmentsToProcess.contains(fragmentId);
	}

	@Override
	public boolean isProcessedImmutableFragment(String fragmentId) {
		return processedImmutableFragments.contains(fragmentId);
	}

	@Override
	public boolean isProcessedMutableFragment(String fragmentId) {
		return processedMutableFragments.containsKey(fragmentId);
	}

	@Override
	public boolean isProcessedMember(String memberId) {
		return memberRepository.isProcessedMember(memberId);
	}

	@Override
	public String nextQueuedFragment() {
		return fragmentsToProcess.poll();
	}

	@Override
	public String nextExpiredMutableFragment() {
		for (Map.Entry<String, LocalDateTime> entry : processedMutableFragments.entrySet()) {
			LocalDateTime expirationDate = entry.getValue();

			// Only return this mutable fragment if it is expired
			if (expirationDate.isBefore(LocalDateTime.now())) {
				return entry.getKey();
			}
		}

		return null;
	}

	@Override
	protected void addFragmentToProcess(String fragmentId) {
		fragmentsToProcess.add(fragmentId);
	}

	@Override
	protected void removeFragmentToProcess(String fragmentId) {
		fragmentsToProcess.remove(fragmentId);
	}

	@Override
	protected void redirectFragmentToProcess(String fragmentId, String redirectedFragmentId) {
		fragmentsToProcess.remove(fragmentId);
		fragmentsToProcess.add(redirectedFragmentId);
	}

	@Override
	protected void addProcessedImmutableFragment(String fragmentId) {
		processedImmutableFragments.add(fragmentId);
	}

	@Override
	protected void removeProcessedImmutableFragment(String fragmentId) {
		processedImmutableFragments.remove(fragmentId);
	}

	@Override
	protected void addProcessedMutableFragment(String fragmentId, LocalDateTime fragmentExpirationDate) {
		processedMutableFragments.put(fragmentId, Optional.ofNullable(fragmentExpirationDate)
				.orElse(LocalDateTime.now().plusSeconds(fragmentExpirationInterval)));
	}

	@Override
	protected LocalDateTime getProcessedMutableFragmentExpirationDate(String fragmentId) {
		return processedMutableFragments.get(fragmentId);
	}

	@Override
	protected void removeProcessedMutableFragment(String fragmentId) {
		processedMutableFragments.remove(fragmentId);
	}

	@Override
	protected void addProcessedMember(String memberId) {
		memberRepository.addProcessedMember(memberId);
	}

	@Override
	protected void removeProcessedMembers() {
		memberRepository.removeProcessedMembers();
	}

	@Override
	public long countQueuedFragments() {
		return fragmentsToProcess.size();
	}

	@Override
	public long countProcessedImmutableFragments() {
		return processedImmutableFragments.size();
	}

	@Override
	public long countProcessedMutableFragments() {
		return processedMutableFragments.size();
	}

	@Override
	public long countProcessedMembers() {
		return memberRepository.countProcessedMembers();
	}

	@Override
	public void clearState() {
		fragmentsToProcess.clear();
		processedImmutableFragments.clear();
		processedMutableFragments.clear();
		memberRepository.removeProcessedMembers();
	}
}
