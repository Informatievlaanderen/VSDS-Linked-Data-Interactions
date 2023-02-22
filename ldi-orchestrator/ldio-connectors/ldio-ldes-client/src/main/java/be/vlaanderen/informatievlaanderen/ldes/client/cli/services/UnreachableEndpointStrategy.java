package be.vlaanderen.informatievlaanderen.ldes.client.cli.services;

public interface UnreachableEndpointStrategy {

	/**
	 * Called when an unreachable endpoint is encountered.
	 *
	 * @return {@literal true} when the caller should proceed, {@literal false} when
	 *         the caller should continue.
	 */
	boolean handleUnreachableEndpoint();
}
