package ldes.client.treenodefetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import ldes.client.treenodefetcher.domain.valueobjects.ModelResponse;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
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

	public List<String> fetchTreeRelations(TreeNodeRequest treeNodeRequest) {
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

	private List<String> createOkResponse(TreeNodeRequest treeNodeRequest, Response response) {
		final InputStream responseBody = response.getBody().map(ByteArrayInputStream::new).orElseThrow();
		final Model model = RDFParser.source(responseBody).forceLang(treeNodeRequest.getLang()).base(treeNodeRequest.getTreeNodeUrl()).toModel();
		return new ModelResponse(model).getRelations();
	}

	private List<String> createRedirectResponse(Response response) {
		return List.of(response.getRedirectLocation().orElseThrow(() -> new IllegalStateException("No Location Header in redirect")));
	}
}
