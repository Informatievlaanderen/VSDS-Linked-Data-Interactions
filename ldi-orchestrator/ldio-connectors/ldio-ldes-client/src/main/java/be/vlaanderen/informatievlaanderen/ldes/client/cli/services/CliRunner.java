package be.vlaanderen.informatievlaanderen.ldes.client.cli.services;

public class CliRunner implements Runnable {

	private boolean threadrunning = true;
	private boolean endpointAvailable = false;
	private final EndpointChecker endpointChecker;
	private final FragmentProcessor fragmentProcessor;
	private final UnreachableEndpointStrategy unreachableEndpointStrategy;

	public CliRunner(FragmentProcessor fragmentProcessor, EndpointChecker endpointChecker,
			UnreachableEndpointStrategy unreachableEndpointStrategy) {
		this.fragmentProcessor = fragmentProcessor;
		this.endpointChecker = endpointChecker;
		this.unreachableEndpointStrategy = unreachableEndpointStrategy;
	}

	@Override
	public void run() {
		while (threadrunning) {
			if (isEndpointAlreadyAvailable()) {
				fragmentProcessor.processLdesFragments();
			} else {
				threadrunning = unreachableEndpointStrategy.handleUnreachableEndpoint();
			}
		}
	}

	private boolean isEndpointAlreadyAvailable() {
		if (!endpointAvailable && endpointChecker.isReachable()) {
			endpointAvailable = true;
		}
		return endpointAvailable;
	}

	public void setThreadrunning(boolean threadrunning) {
		this.threadrunning = threadrunning;
	}
}
