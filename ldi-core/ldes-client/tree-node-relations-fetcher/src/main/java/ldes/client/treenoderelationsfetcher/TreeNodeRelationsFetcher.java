package ldes.client.treenoderelationsfetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.ModelResponse;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeNodeRelation;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeNodeRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class TreeNodeRelationsFetcher {
	private final RequestExecutor requestExecutor;

	public TreeNodeRelationsFetcher(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	public List<TreeNodeRelation> fetchTreeRelations(TreeNodeRequest treeNodeRequest) throws UnsupportedOperationException {
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

	private List<TreeNodeRelation> createOkResponse(TreeNodeRequest treeNodeRequest, Response response) {
		final InputStream responseBody = response.getBody().map(ByteArrayInputStream::new).orElseThrow();
		final Model model = RDFParser.source(responseBody).forceLang(treeNodeRequest.getLang()).base(treeNodeRequest.getTreeNodeUrl()).toModel();
		return new ModelResponse(model).getTreeNodeRelations();
	}

	private List<TreeNodeRelation> createRedirectResponse(Response response) {
		return List.of(
				response.getRedirectLocation()
						.map(url -> new TreeNodeRelation(url, ModelFactory.createDefaultModel()))
						.orElseThrow(() -> new IllegalStateException("No Location Header in redirect"))
		);
	}
}
