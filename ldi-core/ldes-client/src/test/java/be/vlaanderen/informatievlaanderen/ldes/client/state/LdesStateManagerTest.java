package be.vlaanderen.informatievlaanderen.ldes.client.state;

import be.vlaanderen.informatievlaanderen.ldes.client.LdesClientImplFactory;
import be.vlaanderen.informatievlaanderen.ldes.client.config.LdesClientConfig;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesMember;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_FRAGMENT_EXPIRATION_INTERVAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
abstract class LdesStateManagerTest {

	public static final int HTTP_PORT = 10101;

	private final Long testFragmentExpirationInterval = DEFAULT_FRAGMENT_EXPIRATION_INTERVAL;

	protected final String PERSISTENCE_NONE = "application-no-persistence.properties";
	protected final String PERSISTENCE_SQLITE = "application-sqlite-persistence.properties";

	private final LocalDateTime fragmentExpirationDate = LocalDateTime.now()
			.plusSeconds(testFragmentExpirationInterval);

	private final LdesFragment fragmentToProcess = new LdesFragment("localhost:" + HTTP_PORT + "/testData?1",
			fragmentExpirationDate);
	private final LdesFragment nextFragmentToProcess = new LdesFragment("localhost:" + HTTP_PORT + "/testData?2",
			fragmentExpirationDate);

	private final LdesFragment expirationDateSetFragment = new LdesFragment(
			"http://localhost:" + HTTP_PORT + "/expiration-date-set", LocalDateTime.now().minusMonths(1));
	private final LdesFragment expirationDateNotSetFragment = new LdesFragment(
			"http://localhost:" + HTTP_PORT + "/expiration-date-not-set", null);

	private final LdesMember memberToProcess = new LdesMember("localhost:" + HTTP_PORT + "/api/v1/data/10228974/2397",
			null);

	abstract LdesClientConfig getConfig();

	protected LdesStateManager stateManager;

	@BeforeEach
	public void init() {
		stateManager = LdesClientImplFactory.getStateManager(getConfig());

		stateManager.setFragmentExpirationInterval(testFragmentExpirationInterval);
		stateManager.clearState();
		stateManager.queueFragment(fragmentToProcess.getFragmentId());
	}

	@AfterEach
	void tearDown() {
		stateManager.destroyState();
	}

	private void processFragment(LdesFragment fragment) {
		fragment.setImmutable(true);
		stateManager.processedFragment(fragment);
		assertFalse(stateManager.hasNext());
	}

	@Test
	void whenStateManagerIsInitialized_QueueHasOnlyOneItemAndReturnsNullOtherwise() {
		assertTrue(stateManager.hasNext());
		assertEquals(fragmentToProcess.getFragmentId(), stateManager.next());

		processFragment(fragmentToProcess);
		assertNull(stateManager.next());
	}

	@Test
	void whenTryingToQueueSameFragmentTwice_FragmentDoesNotGetAddedToQueue() {
		assertTrue(stateManager.hasNext());
		assertEquals(1, stateManager.countQueuedFragments());
		stateManager.queueFragment(fragmentToProcess.getFragmentId());
		assertTrue(stateManager.hasNext());
		assertEquals(1, stateManager.countQueuedFragments());

		String nextFragment = stateManager.next();
		assertEquals(fragmentToProcess.getFragmentId(), nextFragment);
		processFragment(fragmentToProcess);

		stateManager.queueFragment(nextFragmentToProcess.getFragmentId());
		assertTrue(stateManager.hasNext());
		assertEquals(nextFragmentToProcess.getFragmentId(), stateManager.next());
		processFragment(nextFragmentToProcess);

		stateManager.queueFragment(nextFragmentToProcess.getFragmentId());
		assertFalse(stateManager.hasNext());
		assertEquals(0, stateManager.countQueuedFragments());
	}

	@Test
	void whenQueueingAndProcessingMultipleFragments_queueIsAsExpected() {
		String nextFragment;

		nextFragment = stateManager.next();
		assertEquals(fragmentToProcess.getFragmentId(), nextFragment);
		processFragment(fragmentToProcess);

		stateManager.queueFragment(nextFragmentToProcess.getFragmentId());
		assertTrue(stateManager.hasNext());

		nextFragment = stateManager.next();
		assertEquals(nextFragmentToProcess.getFragmentId(), nextFragment);
		processFragment(nextFragmentToProcess);
	}

	@Test
	void whenTryingToProcessAnAlreadyProcessedLdesMember_MemberDoesNotGetProcessed() {
		assertTrue(stateManager.shouldProcessMember(memberToProcess.getMemberId()));
		assertEquals(0, stateManager.countProcessedMembers());

		stateManager.processedMember(memberToProcess);

		assertFalse(stateManager.shouldProcessMember(memberToProcess.getMemberId()));
		assertEquals(1, stateManager.countProcessedMembers());

		stateManager.removeProcessedMembers();

		assertTrue(stateManager.shouldProcessMember(memberToProcess.getMemberId()));
		assertEquals(0, stateManager.countProcessedMembers());
	}

	@Test
	void whenParsingImmutableFragment_saveAsProcessedPageWithEmptyExpireDate() {
		fragmentToProcess.setImmutable(true);

		assertEquals(0, stateManager.countProcessedImmutableFragments());
		stateManager.processedFragment(fragmentToProcess);
		assertEquals(1, stateManager.countProcessedImmutableFragments());
	}

	@Test
	void whenAfterFirstProcessing_fragmentIsEitherMutableOrImmutable() {
		fragmentToProcess.setImmutable(true);

		stateManager.processedFragment(fragmentToProcess);

		boolean isFragmentProcessed = stateManager.isProcessedImmutableFragment(fragmentToProcess.getFragmentId())
				|| stateManager.isProcessedMutableFragment(fragmentToProcess.getFragmentId());

		assertFalse(stateManager.isQueuedFragment(fragmentToProcess.getFragmentId()));
		assertTrue(isFragmentProcessed);
	}

	@Test
	void whenProcessedFragmentIsImmutable_isContainedInImmutableFragmentQueue() {
		fragmentToProcess.setImmutable(true);

		stateManager.processedFragment(fragmentToProcess);

		assertFalse(stateManager.isQueuedFragment(fragmentToProcess.getFragmentId()));
		assertTrue(stateManager.isProcessedImmutableFragment(fragmentToProcess.getFragmentId()));
		assertFalse(stateManager.isProcessedMutableFragment(fragmentToProcess.getFragmentId()));
	}

	@Test
	void whenProcessedFragmentIsMutable_isContainedInMutableFragmentQueue() {
		fragmentToProcess.setImmutable(false);

		stateManager.processedFragment(fragmentToProcess);

		assertFalse(stateManager.isQueuedFragment(fragmentToProcess.getFragmentId()));
		assertFalse(stateManager.isProcessedImmutableFragment(fragmentToProcess.getFragmentId()));
		assertTrue(stateManager.isProcessedMutableFragment(fragmentToProcess.getFragmentId()));
	}

	@Test
	void whenOnlyImmutableFragments_QueueRemainsEmpty() {
		processFragment(fragmentToProcess);
	}

	@Test
	void whenMutableFragmentsPresent_thenReturnedExpiredOnes() {
		stateManager.queueFragment(fragmentToProcess.getFragmentId());

		fragmentToProcess.setImmutable(false);
		fragmentToProcess.setExpirationDate(LocalDateTime.now().minusDays(30));
		stateManager.processedFragment(fragmentToProcess);

		assertEquals(fragmentToProcess.getFragmentId(), stateManager.nextExpiredMutableFragment());
	}

	@Test
	void whenFragmentCantBeQueued_thenAnLdesExceptionIsThrown() {
		String fragmentId = fragmentToProcess.getFragmentId();

		// stateManager already has the fragmentToProcess
		assertFalse(stateManager.queueFragment(fragmentId));

		// Now mess it up
		stateManager.removeFragmentToProcess(fragmentId);
		stateManager.addProcessedImmutableFragment(fragmentId);
		assertFalse(stateManager.queueFragment(fragmentId));

		// Remove from processed immutable fragments
		stateManager.removeProcessedImmutableFragment(fragmentId);
		assertTrue(stateManager.queueFragment(fragmentId));

		// Now try the exception
		stateManager.removeFragmentToProcess(fragmentId);
		stateManager.addProcessedMutableFragment(fragmentId, LocalDateTime.now());
	}

	@Test
	void whenFragmentIsProcessed_thenExpectedExpirationDateIsSet() {
		LocalDateTime expirationDate;

		expirationDateSetFragment.setImmutable(false);
		stateManager.processedFragment(expirationDateSetFragment);
		assertTrue(stateManager.isProcessedMutableFragment(expirationDateSetFragment.getFragmentId()));
		expirationDate = stateManager
				.getProcessedMutableFragmentExpirationDate(expirationDateSetFragment.getFragmentId());
		assertTrue(expirationDate.isBefore(LocalDateTime.now()));

		expirationDateNotSetFragment.setImmutable(false);
		stateManager.processedFragment(expirationDateNotSetFragment);
		assertTrue(stateManager.isProcessedMutableFragment(expirationDateNotSetFragment.getFragmentId()));
		expirationDate = stateManager
				.getProcessedMutableFragmentExpirationDate(expirationDateNotSetFragment.getFragmentId());
		assertTrue(expirationDate.isAfter(LocalDateTime.now()));

	}
}
