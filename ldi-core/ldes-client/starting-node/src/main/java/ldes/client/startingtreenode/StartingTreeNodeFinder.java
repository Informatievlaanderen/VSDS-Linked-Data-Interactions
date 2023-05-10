package ldes.client.startingtreenode;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.startingtreenode.domain.valueobjects.*;
import ldes.client.startingtreenode.exception.StartingNodeNotFoundException;
import org.apache.http.HttpHeaders;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StartingTreeNodeFinder {

	private final Logger log = LoggerFactory.getLogger(StartingTreeNodeFinder.class);

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
		log.atInfo().log("determineStartingTreeNode for: " + startingNodeRequest.url());
		RequestHeaders requestHeaders = new RequestHeaders(
				List.of(new RequestHeader(HttpHeaders.ACCEPT, startingNodeRequest.contentType())));
		Response response = requestExecutor.execute(new Request(startingNodeRequest.url(), requestHeaders));
		if (response.isOk()) {
			return selectStartingNode(startingNodeRequest, response);
		}
		if (response.isRedirect()) {
			StartingNodeRequest newStartingNodeRequest = startingNodeRequest
					.createRedirectedEndpoint(response.getRedirectLocation()
							.orElseThrow(() -> new StartingNodeNotFoundException(startingNodeRequest.url(),
									"No Location Header in redirect.")));
			return determineStartingTreeNode(newStartingNodeRequest);
		}
		throw new StartingNodeNotFoundException(startingNodeRequest.url(),
				"Unable to hande response " + response.getHttpStatus());
	}

	private StartingTreeNode selectStartingNode(StartingNodeRequest startingNodeRequest, Response response) {
		log.atInfo().log("Parsing response for: " + startingNodeRequest.url());
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
