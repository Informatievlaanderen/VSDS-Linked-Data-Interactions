package be.vlaanderen.informatievlaanderen.ldes.client.state;

import be.vlaanderen.informatievlaanderen.ldes.client.LdesClientImplFactory;
import be.vlaanderen.informatievlaanderen.ldes.client.config.LdesClientConfig;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesService;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesMember;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.LocalDateTime;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@WireMockTest(httpPort = LdesStateManagerInteractionTest.HTTP_PORT)
class LdesStateManagerInteractionTest {

	public static final int HTTP_PORT = 10101;

	private LdesService ldesService;
	private LdesStateManager stateManager;

	private final LocalDateTime fragmentExpiration = LocalDateTime.now().plusHours(1);

	private final LdesFragment fragment3Redirect = new LdesFragment("http://localhost:" + HTTP_PORT + "/exampleData",
			fragmentExpiration);
	private final LdesFragment fragment3 = new LdesFragment(
			"http://localhost:" + HTTP_PORT + "/exampleData?generatedAtTime=2022-05-03T00:00:00.000Z",
			fragmentExpiration);

	private final LdesFragment fragment4 = new LdesFragment(
			"http://localhost:" + HTTP_PORT + "/exampleData?generatedAtTime=2022-05-04T00:00:00.000Z",
			fragmentExpiration);

	private final LdesFragment fragment5 = new LdesFragment(
			"http://localhost:" + HTTP_PORT + "/exampleData?generatedAtTime=2022-05-05T00:00:00.000Z",
			fragmentExpiration);

	private final LdesMember fragment3Member1 = new LdesMember("http://localhost:10101/exampleData/10054228/165875",
			null);
	private final LdesMember fragment3Member2 = new LdesMember("http://localhost:10101/exampleData/10054228/165876",
			null);
	private final LdesMember fragment4Member1 = new LdesMember("http://localhost:10101/exampleData/10034919/29796",
			null);
	private final LdesMember fragment4Member2 = new LdesMember("http://localhost:10101/exampleData/10034919/29797",
			null);
	private final LdesMember fragment5Member1 = new LdesMember(
			"localhost:10101/api/v1/mobility-hindrances/10054228/165874", null);

	@BeforeEach
	void init() {
		LdesClientConfig config = new LdesClientConfig();

		stateManager = LdesClientImplFactory.getStateManager(config);
		ldesService = new LdesServiceImpl(stateManager, LdesClientImplFactory.getFragmentFetcher(config));

		assertEquals(SqlitePersistedLdesStateManager.class.getName(), stateManager.getClass().getName());

		stateManager.clearState();
	}

	@AfterEach
	void tearDown() {
		ldesService.getStateManager().destroyState();
	}

	@Test
	void clearState() {
		stateManager.addFragmentToProcess(fragment3.getFragmentId());
		stateManager.addProcessedImmutableFragment(fragment4.getFragmentId());
		stateManager.addProcessedMutableFragment(fragment5.getFragmentId(), fragmentExpiration);
		stateManager.processedMember(new LdesMember("memberId", null));

		assertEquals(1, stateManager.countQueuedFragments());
		assertEquals(1, stateManager.countProcessedImmutableFragments());
		assertEquals(1, stateManager.countProcessedMutableFragments());
		assertEquals(1, stateManager.countProcessedMembers());

		stateManager.clearState();

		assertEquals(0, stateManager.countQueuedFragments());
		assertEquals(0, stateManager.countProcessedImmutableFragments());
		assertEquals(0, stateManager.countProcessedMutableFragments());
		assertEquals(0, stateManager.countProcessedMembers());
	}

	@Test
	void isQueuedFragment() {
		assertFalse(stateManager.isQueuedFragment(fragment3.getFragmentId()));
		assertFalse(stateManager.isProcessedImmutableFragment(fragment3.getFragmentId()));
		assertFalse(stateManager.isProcessedMutableFragment(fragment3.getFragmentId()));

		ldesService.queueFragment(fragment3.getFragmentId());

		assertTrue(ldesService.hasFragmentsToProcess());
		assertTrue(stateManager.isQueuedFragment(fragment3.getFragmentId()));
	}

	@Test
	void isProcessedImmutableFragment() {
		ldesService.queueFragment(fragment3.getFragmentId());

		assertTrue(stateManager.hasNext());
		ldesService.processNextFragment();
		assertTrue(stateManager.isProcessedImmutableFragment(fragment3.getFragmentId()));

		assertTrue(stateManager.hasNext());
		ldesService.processNextFragment();
		assertTrue(stateManager.isProcessedImmutableFragment(fragment4.getFragmentId()));

		assertTrue(stateManager.hasNext());
		ldesService.processNextFragment();
		assertTrue(stateManager.isProcessedImmutableFragment(fragment5.getFragmentId()));

		assertEquals(0, stateManager.countQueuedFragments());
		assertEquals(5, stateManager.countProcessedMembers());
	}

	@Test
	void isProcessedMutableFragment() {
		boolean immutable = fragment3.isImmutable();
		fragment3.setImmutable(false);

		stateManager.processedFragment(fragment3);

		assertTrue(stateManager.isProcessedMutableFragment(fragment3.getFragmentId()));

		fragment3.setImmutable(immutable);
	}

	@Test
	void redirectFragmentToProcess() {
		stateManager.queueFragment(fragment3Redirect.getFragmentId());
		assertTrue(stateManager.isQueuedFragment(fragment3Redirect.getFragmentId()));

		stateManager.redirectFragment(fragment3Redirect.getFragmentId(), fragment3.getFragmentId());
		assertTrue(stateManager.isQueuedFragment(fragment3.getFragmentId()));
		assertFalse(stateManager.isQueuedFragment(fragment3Redirect.getFragmentId()));
	}

}
