package be.vlaanderen.informatievlaanderen.ldes.ldi.client.services;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

class CliRunnerTest {

	private final EndpointChecker endpointChecker = Mockito.mock(EndpointChecker.class);
	private final FragmentProcessor fragmentProcessor = Mockito.mock(FragmentProcessor.class);
	private final UnreachableEndpointStrategy unreachableEndpointStrategy = Mockito
			.mock(UnreachableEndpointStrategy.class);

	private CliRunner cliRunner;

	@BeforeEach
	void setUp() {
		Awaitility.reset();
		cliRunner = new CliRunner(fragmentProcessor, endpointChecker, unreachableEndpointStrategy);
	}

	@Test
	void when_EndpointIsNotReachable_ItIsPeriodicallyPolled() {
		when(endpointChecker.isReachable()).thenReturn(false);

		ExecutorService service = Executors.newSingleThreadExecutor();
		service.submit(cliRunner);

		await().during(10, TimeUnit.MILLISECONDS).until(() -> true);
		cliRunner.setThreadrunning(false);

		verifyNoInteractions(fragmentProcessor);
		verify(unreachableEndpointStrategy, atLeast(1)).handleUnreachableEndpoint();
	}

	@Test
	void when_EndpointIsReachable_FragmentProcessorIsCalled() {
		when(endpointChecker.isReachable()).thenReturn(true);

		ExecutorService service = Executors.newSingleThreadExecutor();
		service.submit(cliRunner);

		await().during(10, TimeUnit.MILLISECONDS).until(() -> true);
		cliRunner.setThreadrunning(false);

		verify(fragmentProcessor, atLeast(1)).processLdesFragments();
		verifyNoInteractions(unreachableEndpointStrategy);
	}

}
