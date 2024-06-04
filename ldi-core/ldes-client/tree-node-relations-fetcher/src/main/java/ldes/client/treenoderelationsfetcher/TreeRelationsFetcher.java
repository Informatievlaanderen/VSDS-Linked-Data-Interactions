package ldes.client.treenoderelationsfetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.ModelResponse;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeNodeRequest;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeRelation;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class TreeRelationsFetcher {
	private final RequestExecutor requestExecutor;

	public TreeRelationsFetcher(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	/**
	 * Fetches a new TreeNode and extract all the relations out of it
	 *
	 * @param treeNodeRequest based on the relations found in the previous TreeNode
	 * @return a new list of all the next TreeNode relations that were present in the received TreeNode
	 */
	public List<TreeRelation> fetchTreeRelations(TreeNodeRequest treeNodeRequest) throws UnsupportedOperationException {
		final Response response = requestExecutor.execute(treeNodeRequest.createRequest());

		if (response.isOk()) {
			return createOkResponse(treeNodeRequest, response);
		}

		if (response.isRedirect()) {
			return createRedirectResponse(response);
		}


		throw new UnsupportedOperationException(
				"Cannot handle response " + response.getHttpStatus() + " of TreeNodeRequest " + treeNodeRequest);
	}

	private List<TreeRelation> createOkResponse(TreeNodeRequest treeNodeRequest, Response response) {
		final InputStream responseBody = response.getBody().map(ByteArrayInputStream::new).orElseThrow();
		final Model model = RDFParser.source(responseBody).forceLang(treeNodeRequest.getLang()).base(treeNodeRequest.getTreeNodeUrl()).toModel();
		return new ModelResponse(model).getTreeRelations();
	}

	private List<TreeRelation> createRedirectResponse(Response response) {
		return List.of(
				response.getRedirectLocation()
						.map(url -> new TreeRelation(url, true))
						.orElseThrow(() -> new IllegalStateException("No Location Header in redirect"))
		);
	}
}
