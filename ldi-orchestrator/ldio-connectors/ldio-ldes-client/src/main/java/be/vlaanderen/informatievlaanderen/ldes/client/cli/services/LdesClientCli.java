package be.vlaanderen.informatievlaanderen.ldes.client.cli.services;

import be.vlaanderen.informatievlaanderen.ldes.client.LdesClientImplFactory;
import be.vlaanderen.informatievlaanderen.ldes.client.cli.model.EndpointBehaviour;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.EndpointRequester;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint.Endpoint;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode.StartingNode;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesService;
import org.apache.jena.riot.Lang;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.util.concurrent.ExecutorService;

@Component
public class LdesClientCli {

	private final ExecutorService executorService;
	private final LdesService ldesService = LdesClientImplFactory.getLdesService();
	private final EndpointRequester endpointRequester;

	private static final PrintStream OUTPUT_STREAM = System.out;

	public LdesClientCli(ExecutorService executorService, EndpointRequester endpointRequester) {
		this.executorService = executorService;
		this.endpointRequester = endpointRequester;
	}

	public void start(String fragmentId, Lang dataSourceFormat, Lang dataDestinationFormat, Long expirationInterval,
			Long pollingInterval, EndpointBehaviour endpointBehaviour) {
		UnreachableEndpointStrategy unreachableEndpointStrategy = getUnreachableEndpointStrategy(endpointBehaviour,
				fragmentId, pollingInterval);
		ldesService.setDataSourceFormat(dataSourceFormat);
		ldesService.setFragmentExpirationInterval(expirationInterval);
		ldesService.queueFragment(getStartingUrl(fragmentId, dataSourceFormat));

		FragmentProcessor fragmentProcessor = new FragmentProcessor(ldesService, OUTPUT_STREAM, dataDestinationFormat,
				pollingInterval);
		EndpointChecker endpointChecker = new EndpointChecker(fragmentId);
		CliRunner cliRunner = new CliRunner(fragmentProcessor, endpointChecker, unreachableEndpointStrategy);

		executorService.submit(cliRunner);
		executorService.shutdown();
	}

	private String getStartingUrl(String fragmentId, Lang dataSourceFormat) {
		return endpointRequester
				.determineStartingNode(new Endpoint(fragmentId, dataSourceFormat))
				.map(StartingNode::url)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Starting url could not be determined for fragmentId: " + fragmentId));
	}

	public LdesService getLdesService() {
		return ldesService;
	}

	protected UnreachableEndpointStrategy getUnreachableEndpointStrategy(EndpointBehaviour endpointBehaviour,
			String fragmentId, long pollingInterval) {
		switch (endpointBehaviour) {
			case STOPPING -> {
				return new StoppingStrategy(fragmentId);
			}
			case WAITING -> {
				return new WaitingStrategy(pollingInterval);
			}
			default -> throw new IllegalArgumentException();
		}
	}
}
