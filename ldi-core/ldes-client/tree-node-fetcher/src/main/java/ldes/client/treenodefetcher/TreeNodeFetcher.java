package ldes.client.treenodefetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.domain.valueobjects.ModelResponse;
import ldes.client.treenodefetcher.domain.valueobjects.MutabilityStatus;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;

import java.time.LocalDateTime;
import java.util.List;

public class TreeNodeFetcher {

	private final RequestExecutor requestExecutor;

	public TreeNodeFetcher(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	public TreeNodeResponse fetchTreeNode(TreeNodeRequest treeNodeRequest) {
		final Response response = requestExecutor.execute(treeNodeRequest.createRequest());

		return switch (response.getHttpStatus()) {
			case HttpStatus.SC_OK -> createOkResponse(treeNodeRequest, response);
			case HttpStatus.SC_MOVED_TEMPORARILY -> createMovedTemporarilyResponse(response);
			case HttpStatus.SC_NOT_MODIFIED -> createNotModifiedResponse(response);
			default -> throw new UnsupportedOperationException(
					"Cannot handle response " + response.getHttpStatus() + " of TreeNodeRequest " + treeNodeRequest);
		};
	}

	private TreeNodeResponse createOkResponse(TreeNodeRequest treeNodeRequest, Response response) {
		final String responseBody = response.getBody().orElseThrow();
		final Model model = RDFParser.fromString(responseBody).forceLang(treeNodeRequest.getLang()).toModel();
		final ModelResponse modelResponse = new ModelResponse(model);
		final MutabilityStatus mutabilityStatus = getMutabilityStatus(response);
		return new TreeNodeResponse(modelResponse.getRelations(), modelResponse.getMembers(), mutabilityStatus);
	}

	private static TreeNodeResponse createMovedTemporarilyResponse(Response response) {
		return new TreeNodeResponse(
				List.of(response.getFirstHeaderValue(HttpHeaders.LOCATION).orElseThrow()),
				List.of(),
				new MutabilityStatus(false, LocalDateTime.MAX));
	}

	private static TreeNodeResponse createNotModifiedResponse(Response response) {
		return new TreeNodeResponse(List.of(), List.of(), getMutabilityStatus(response));
	}

	private static MutabilityStatus getMutabilityStatus(Response response) {
		return response.getFirstHeaderValue(HttpHeaders.CACHE_CONTROL)
				.map(MutabilityStatus::ofHeader)
				.orElseGet(MutabilityStatus::empty);
	}

}
