package be.vlaanderen.informatievlaanderen.ldes.client.state;

import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_FRAGMENT_EXPIRATION_INTERVAL;

public abstract class LdesStateManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdesStateManager.class);

	protected Long fragmentExpirationInterval;

	public abstract boolean destroyState();

	public abstract boolean hasQueuedFragments();

	public abstract long countQueuedFragments();

	public abstract long countProcessedImmutableFragments();

	public abstract long countProcessedMutableFragments();

	public abstract long countProcessedMembers();

	public abstract void clearState();

	protected abstract boolean isQueuedFragment(String fragmentId);

	protected abstract boolean isProcessedImmutableFragment(String fragmentId);

	protected abstract boolean isProcessedMutableFragment(String fragmentId);

	public abstract boolean isProcessedMember(String memberId);

	protected abstract String nextQueuedFragment();

	protected abstract String nextExpiredMutableFragment();

	protected abstract void addFragmentToProcess(String fragmentId);

	protected abstract void redirectFragmentToProcess(String fragmentId, String redirectedFragmentId);

	protected abstract void removeFragmentToProcess(String fragmentId);

	protected abstract void addProcessedImmutableFragment(String fragmentId);

	protected abstract void removeProcessedImmutableFragment(String fragmentId);

	protected abstract void addProcessedMutableFragment(String fragmentId, LocalDateTime fragmentExpirationDate);

	protected abstract LocalDateTime getProcessedMutableFragmentExpirationDate(String fragmentId);

	protected abstract void removeProcessedMutableFragment(String fragmentId);

	protected abstract void addProcessedMember(String memberId);

	protected abstract void removeProcessedMembers();

	protected LdesStateManager() {
		setFragmentExpirationInterval(DEFAULT_FRAGMENT_EXPIRATION_INTERVAL);
	}

	public Long getFragmentExpirationInterval() {
		return fragmentExpirationInterval;
	}

	public void setFragmentExpirationInterval(Long fragmentExpirationInterval) {
		this.fragmentExpirationInterval = fragmentExpirationInterval;
	}

	/**
	 * Indicates if there are fragments in the queue or mutable fragments that have
	 * reached their expiration date.
	 *
	 * An implementation should be able to queue fragments and when processed,
	 * distinguish between immutable and mutable fragments. Immutable fragments can
	 * disappear from the queue entirely (but should be recognized as not queueable
	 * in the future). Mutable fragments need to be processed again based on their
	 * expiration date. This date can either be provided by the data producer or a
	 * by setting a configurable reasonable default.
	 *
	 * @return true if there are fragments that can be processed, either from the
	 *         queue or because they are mutable and their expiration date has been
	 *         reached, false otherwise.
	 */
	public boolean hasNext() {
		return hasQueuedFragments() || nextExpiredMutableFragment() != null;
	}

	/**
	 * Return the next queued fragment or the next expired or expiration-date-less
	 * mutable fragment).
	 *
	 * If there are more fragments queued, return the next one. If the next mutable
	 * fragment has no expiration date set, return it. If it has an expiration date,
	 * return it only if the fragment has expired.
	 *
	 * @return the fragment id (URL) of the next fragment
	 */
	public String next() {
		if (hasQueuedFragments()) {
			String fragmentId = nextQueuedFragment();

			LOGGER.debug("NEXT FRAGMENT: queued fragment {}", fragmentId);

			return fragmentId;
		}

		String fragmentId = nextExpiredMutableFragment();
		if (fragmentId != null) {
			LOGGER.debug("NEXT FRAGMENT: expired fragment {}", fragmentId);

			return fragmentId;
		}

		LOGGER.debug("NEXT FRAGMENT: none");

		return null;
	}

	/**
	 * Returns true if the fragment was queued, false otherwise.
	 *
	 * <ol>
	 * <li>If the fragments queue already contains the fragment id, don't queue it
	 * and return false.</li>
	 * <li>If the fragment is immutable and was already processed, don't queue it
	 * and return false.</li>
	 * <li>If the fragment is not in the processed mutable fragments queue, queue it
	 * and return true.</li>
	 * </ol>
	 *
	 * @param fragmentId
	 *            the id of the fragment to queue
	 * @return true if the fragment was queued, false otherwise
	 */
	public final boolean queueFragment(String fragmentId) {
		if (isQueuedFragment(fragmentId)) {
			LOGGER.debug("QUEUE: Not queueing already queued fragment {}", fragmentId);
			return false;
		}

		if (isProcessedImmutableFragment(fragmentId)) {
			LOGGER.debug("QUEUE: Not queueing processed immutable fragment {}", fragmentId);
			return false;
		}

		if (!isProcessedMutableFragment(fragmentId)) {
			addFragmentToProcess(fragmentId);

			LOGGER.debug("QUEUE: Queued fragment {}", fragmentId);
			return true;
		}

		return false;
	}

	public void redirectFragment(String fragmentId, String redirectedFragmentId) {
		redirectFragmentToProcess(fragmentId, redirectedFragmentId);
	}

	public void processedFragment(LdesFragment fragment) {
		String fragmentId = fragment.getFragmentId();

		if (fragment.isImmutable()) {
			addProcessedImmutableFragment(fragmentId);

			removeProcessedMutableFragment(fragmentId);

			LOGGER.debug("PROCESSED IMMUTABLE FRAGMENT {}", fragmentId);
		} else {
			addProcessedMutableFragment(fragmentId, Optional.ofNullable(fragment.getExpirationDate())
					.orElse(LocalDateTime.now().plusSeconds(fragmentExpirationInterval)));


			LOGGER.debug("PROCESSED MUTABLE FRAGMENT {}", fragmentId);
		}

		removeFragmentToProcess(fragmentId);
	}

	public final void processedMember(LdesMember member) {
		addProcessedMember(member.getMemberId());
	}

	public boolean shouldProcessMember(String memberId) {
		return !isProcessedMember(memberId);
	}
}
