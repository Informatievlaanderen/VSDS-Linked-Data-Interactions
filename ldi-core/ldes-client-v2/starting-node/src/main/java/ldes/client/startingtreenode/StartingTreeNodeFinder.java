package ldes.client.startingtreenode;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeader;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.startingtreenode.domain.valueobjects.Endpoint;
import ldes.client.startingtreenode.domain.valueobjects.StartingNodeSpecification;
import ldes.client.startingtreenode.domain.valueobjects.TreeNode;
import ldes.client.startingtreenode.domain.valueobjects.TreeNodeSpecification;
import ldes.client.startingtreenode.domain.valueobjects.ViewSpecification;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class StartingTreeNodeFinder {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartingTreeNodeFinder.class);
	private final RequestExecutor requestExecutor;
	private final List<StartingNodeSpecification> startingNodeSpecifications;

	public StartingTreeNodeFinder(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
		startingNodeSpecifications = List.of(new ViewSpecification(), new TreeNodeSpecification());
	}

	/**
	 * Determines the first node to be queued.
	 *
	 * @param endpoint
	 *            can contain a collection, view or treeNode.
	 * @return the first node to be queued by the client
	 */
	public Optional<TreeNode> determineStartingTreeNode(final Endpoint endpoint) {
		LOGGER.info("Determining starting node for: {}", endpoint.url());
		RequestHeaders requestHeaders = new RequestHeaders(
				List.of(new RequestHeader(HttpHeaders.ACCEPT, endpoint.contentType())));
		Response response = requestExecutor.execute(new Request(endpoint.url(), requestHeaders));
		if (responseIsOK(response)) {
			return selectStartingNode(endpoint, response);
		}
		if (responseIsRedirect(response)) {
			Endpoint newEndpoint = endpoint
					.createRedirectedEndpoint(response.getValueOfHeader(HttpHeaders.LOCATION).orElseThrow());
			return determineStartingTreeNode(newEndpoint);
		}
		return Optional.empty();
	}

	private Optional<TreeNode> selectStartingNode(Endpoint endpoint, Response response) {
		Model model = RDFParser
				.fromString(response.getBody().orElseThrow())
				.lang(endpoint.lang())
				.build()
				.toModel();
		Optional<TreeNode> startingNode = startingNodeSpecifications
				.stream()
				.filter(startingNodeSpecification -> startingNodeSpecification.test(model))
				.map(startingNodeSpecification -> startingNodeSpecification.extractStartingNode(model))
				.findFirst();
		LOGGER.info("Selected starting node: " + startingNode.map(TreeNode::getUrl).orElse("empty"));
		return startingNode;
	}

	private boolean responseIsOK(Response response) {
		return response.getHttpStatus() == HttpStatus.SC_OK;
	}

	private boolean responseIsRedirect(Response response) {
		return response.getHttpStatus() == HttpStatus.SC_MOVED_TEMPORARILY
				&& response.getValueOfHeader(HttpHeaders.LOCATION).isPresent();
	}

}
