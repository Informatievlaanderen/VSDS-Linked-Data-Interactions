package be.vlaanderen.informatievlaanderen.ldes.client.cli.services;

import be.vlaanderen.informatievlaanderen.ldes.client.LdesClientImplFactory;
import be.vlaanderen.informatievlaanderen.ldes.client.cli.model.EndpointBehaviour;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.EndpointRequester;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint.Endpoint;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode.StartingNode;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.apache.jena.riot.Lang;

import java.util.concurrent.ExecutorService;

public class LdesClientCli extends LdiInput {

	private final LdesService ldesService = LdesClientImplFactory.getLdesService();
	private final EndpointRequester endpointRequester;

	public LdesClientCli(ExecutorService executorService, EndpointRequester endpointRequester,
			ComponentExecutor executor, String fragmentId, Lang dataSourceFormat, Long expirationInterval,
			Long pollingInterval, EndpointBehaviour endpointBehaviour) {
		super(executor, null);
		this.endpointRequester = endpointRequester;

		UnreachableEndpointStrategy unreachableEndpointStrategy = getUnreachableEndpointStrategy(endpointBehaviour,
				fragmentId, pollingInterval);
		ldesService.setDataSourceFormat(dataSourceFormat);
		ldesService.setFragmentExpirationInterval(expirationInterval);
		ldesService.queueFragment(getStartingUrl(fragmentId, dataSourceFormat));

		FragmentProcessor fragmentProcessor = new FragmentProcessor(ldesService, executor, pollingInterval);
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

	@Override
	public LdiInput withExecutor(ComponentExecutor executor) {
		return null;
	}

	@Override
	public LdiInput withAdapter(LdiAdapter adapter) {
		return null;
	}
}
