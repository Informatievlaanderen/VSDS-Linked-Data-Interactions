package ldes.client.startingtreenode;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeader;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.startingtreenode.domain.valueobjects.*;
import ldes.client.startingtreenode.exception.StartingNodeNotFoundException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;

import java.util.List;

public class StartingTreeNodeFinder {

	private final RequestExecutor requestExecutor;
	private final List<StartingNodeSpecification> startingNodeSpecifications;

	public StartingTreeNodeFinder(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
		startingNodeSpecifications = List.of(new ViewSpecification(), new TreeNodeSpecification());
	}

	/**
	 * Determines the first node to be queued.
	 *
	 * @param startingNodeRequest
	 *            can contain a collection, view or treeNode.
	 * @return the first node to be queued by the client
	 */
	public StartingTreeNode determineStartingTreeNode(final StartingNodeRequest startingNodeRequest) {
		RequestHeaders requestHeaders = new RequestHeaders(
				List.of(new RequestHeader(HttpHeaders.ACCEPT, startingNodeRequest.contentType())));
		Response response = requestExecutor.execute(new Request(startingNodeRequest.url(), requestHeaders));
		if (response.hasStatus(HttpStatus.SC_OK)) {
			return selectStartingNode(startingNodeRequest, response);
		}
		if (response.hasStatus(HttpStatus.SC_MOVED_TEMPORARILY)) {
			StartingNodeRequest newStartingNodeRequest = startingNodeRequest
					.createRedirectedEndpoint(response.getValueOfHeader(HttpHeaders.LOCATION)
							.orElseThrow(() -> new StartingNodeNotFoundException(startingNodeRequest.url(),
									"No Location Header in redirect.")));
			return determineStartingTreeNode(newStartingNodeRequest);
		}
		throw new StartingNodeNotFoundException(startingNodeRequest.url(),
				"Unable to hande response " + response.getHttpStatus());
	}

	private StartingTreeNode selectStartingNode(StartingNodeRequest startingNodeRequest, Response response) {
		Model model = RDFParser
				.fromString(response.getBody().orElseThrow())
				.lang(startingNodeRequest.lang())
				.build()
				.toModel();
		return startingNodeSpecifications
				.stream()
				.filter(startingNodeSpecification -> startingNodeSpecification.test(model))
				.map(startingNodeSpecification -> startingNodeSpecification.extractStartingNode(model))
				.findFirst()
				.orElseThrow(() -> new StartingNodeNotFoundException(startingNodeRequest.url(),
						"Starting Node could not be extracted from model."));
	}

}
