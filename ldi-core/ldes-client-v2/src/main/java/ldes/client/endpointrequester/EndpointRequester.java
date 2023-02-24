package ldes.client.endpointrequester;


import ldes.client.endpointrequester.endpoint.Endpoint;
import ldes.client.endpointrequester.endpointresponse.EndpointResponse;
import ldes.client.endpointrequester.endpointresponse.EndpointResponseFactory;
import ldes.client.endpointrequester.endpointresponse.repository.EndpointResponseRepository;
import ldes.client.endpointrequester.endpointresponse.repository.HttpRequestExecutor;
import ldes.client.endpointrequester.startingnode.StartingNode;
import ldes.client.endpointrequester.startingnode.StartingNodeSupplier;
import ldes.client.endpointrequester.startingnode.TreeNodeSupplier;
import ldes.client.endpointrequester.startingnode.ViewNodeSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class EndpointRequester {

	private static final Logger LOGGER = LoggerFactory.getLogger(EndpointRequester.class);

	private final EndpointResponseRepository endpointResponseRepository;
	private final StartingNodeSupplier startingNodeSupplier;

	public EndpointRequester() {
		this(
				new HttpRequestExecutor(new EndpointResponseFactory()),
				new ViewNodeSupplier(new TreeNodeSupplier(null)));
	}

	public EndpointRequester(EndpointResponseRepository endpointResponseRepository,
			StartingNodeSupplier startingNodeSupplier) {
		this.endpointResponseRepository = endpointResponseRepository;
		this.startingNodeSupplier = startingNodeSupplier;
	}

	/**
	 * Determines the first node to be queued.
	 *
	 * @param endpoint
	 *            can contain a collection, view or treeNode.
	 * @return the first node to be queued by the client
	 */
	public Optional<StartingNode> determineStartingNode(final Endpoint endpoint) {
		LOGGER.info("Determining starting node for: {}", endpoint.url());
		final EndpointResponse endpointResponse = endpointResponseRepository.getEndpointResponse(endpoint);
		final Optional<StartingNode> startingNode = startingNodeSupplier.getStartingNode(endpointResponse.model());
		LOGGER.info("Selected starting node: " + startingNode.map(StartingNode::url).orElse("empty"));
		return startingNode;
	}

}