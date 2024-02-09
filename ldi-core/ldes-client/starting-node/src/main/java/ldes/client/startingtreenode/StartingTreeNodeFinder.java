package ldes.client.startingtreenode;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import ldes.client.startingtreenode.domain.valueobjects.*;
import ldes.client.startingtreenode.exception.StartingNodeNotFoundException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.List;

public class StartingTreeNodeFinder {

	private final Logger log = LoggerFactory.getLogger(StartingTreeNodeFinder.class);

	private final RedirectRequestExecutor requestExecutor;
	private final List<StartingNodeSpecification> startingNodeSpecifications;

	public StartingTreeNodeFinder(RequestExecutor requestExecutor) {
		this.requestExecutor = new RedirectRequestExecutor(requestExecutor);
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
		final Response response = requestExecutor.execute(startingNodeRequest);
		final Model model = getModelFromResponse(startingNodeRequest.lang(), response.getBody().orElseThrow(), startingNodeRequest.url());
		return selectStartingNode(startingNodeRequest, model);
	}

	private Model getModelFromResponse(Lang lang, byte[] responseBody, String baseUrl) {
		return RDFParser.source(new ByteArrayInputStream(responseBody)).lang(lang).base(baseUrl).build().toModel();
	}

	private StartingTreeNode selectStartingNode(StartingNodeRequest startingNodeRequest, Model model) {
		log.atInfo().log("Parsing response for: " + startingNodeRequest.url());
		return startingNodeSpecifications
				.stream()
				.filter(startingNodeSpecification -> startingNodeSpecification.test(model))
				.map(startingNodeSpecification -> startingNodeSpecification.extractStartingNode(model))
				.findFirst()
				.orElseThrow(() -> new StartingNodeNotFoundException(startingNodeRequest.url(),
						"Starting Node could not be extracted from model."));
	}

}
