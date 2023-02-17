package be.vlaanderen.informatievlaanderen.ldes.client.cli.state;

import be.vlaanderen.informatievlaanderen.ldes.client.LdesClientImplFactory;
import be.vlaanderen.informatievlaanderen.ldes.client.cli.services.CliRunner;
import be.vlaanderen.informatievlaanderen.ldes.client.cli.services.EndpointChecker;
import be.vlaanderen.informatievlaanderen.ldes.client.cli.services.FragmentProcessor;
import be.vlaanderen.informatievlaanderen.ldes.client.cli.services.WaitingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.client.config.LdesClientConfig;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesService;
import be.vlaanderen.informatievlaanderen.ldes.client.state.LdesStateManager;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Disabled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@WireMockTest(httpPort = 10101)
class LdesClientCliStateTest {

	private final String fragment3 = "http://localhost:10101/exampleData?generatedAtTime=2022-05-03T00:00:00.000Z";
	private final String fragment4 = "http://localhost:10101/exampleData?generatedAtTime=2022-05-04T00:00:00.000Z";
	private final String fragment5 = "http://localhost:10101/exampleData?generatedAtTime=2022-05-05T00:00:00.000Z";

	LdesClientConfig config = new LdesClientConfig();

	@Disabled("in revision")
	void whenLdesClientCliHasReplicated_thenNoFragmentsRemainInTheQueue() throws Exception {
		LdesClientConfig config = new LdesClientConfig();
		LdesService ldesService = LdesClientImplFactory.getLdesService(config);
		LdesStateManager stateManager = ldesService.getStateManager();

		config.setPersistenceDbName("test-completed-ldes.db");
		ldesService.setDataSourceFormat(Lang.JSONLD);
		ldesService.setFragmentExpirationInterval(1000L);
		ldesService.queueFragment(fragment3);

		FragmentProcessor fragmentProcessor = new FragmentProcessor(ldesService, System.out, Lang.TURTLE, 1L);
		EndpointChecker endpointChecker = new EndpointChecker(fragment3);
		CliRunner cliRunner = new CliRunner(fragmentProcessor, endpointChecker, new WaitingStrategy(20L));

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(cliRunner);
		await().atMost(1, TimeUnit.MINUTES).until(stateManager::countQueuedFragments, equalTo(0L));

		assertEquals(0, stateManager.countQueuedFragments());
		assertEquals(3, stateManager.countProcessedImmutableFragments());
		assertEquals(6, stateManager.countProcessedMembers());

		stateManager.destroyState();
	}

	@Disabled("in revision")
	void whenLdesClientCliResumes_thenCliResumesAtLastMutableFragment() {
		LdesClientConfig config = new LdesClientConfig();
		LdesService ldesService = LdesClientImplFactory.getLdesService(config);
		LdesStateManager stateManager = ldesService.getStateManager();

		config.setPersistenceDbName("test-resumed-ldes.db");
		ldesService.setDataSourceFormat(Lang.JSONLD);
		ldesService.setFragmentExpirationInterval(1000L);
		ldesService.queueFragment(fragment3);
		ldesService.getStateManager().clearState();

		FragmentProcessor fragmentProcessor = new FragmentProcessor(ldesService, System.out, Lang.TURTLE, 1L);
		EndpointChecker endpointChecker = new EndpointChecker(fragment3);
		CliRunner cliRunner = new CliRunner(fragmentProcessor, endpointChecker, new WaitingStrategy(20L));

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(cliRunner);
		await().during(10, TimeUnit.MILLISECONDS).until(() -> true);
		cliRunner.setThreadrunning(false);

		verify(fragmentProcessor, atLeast(1)).processLdesFragments();
		assertEquals(0, stateManager.countQueuedFragments());
		executorService.shutdown();

		assertEquals(1, stateManager.countProcessedImmutableFragments());
		assertEquals(fragment4, stateManager.next());

		executorService = Executors.newSingleThreadExecutor();
		executorService.submit(cliRunner);
		await().during(10, TimeUnit.MILLISECONDS).until(() -> true);
		cliRunner.setThreadrunning(false);

		verify(fragmentProcessor, atLeast(1)).processLdesFragments();
		assertEquals(0, stateManager.countQueuedFragments());
		executorService.shutdown();
		assertEquals(2, stateManager.countProcessedImmutableFragments());
		assertEquals(fragment5, stateManager.next());

		stateManager.destroyState();
	}
}
