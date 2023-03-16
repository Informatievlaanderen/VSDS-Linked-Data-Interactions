package ldes.client.treenodefetcher;

import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeResponse;
import ldes.client.treenodefetcher.domain.valueobjects.ModelResponse;
import ldes.client.treenodefetcher.domain.valueobjects.MutabilityStatus;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
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
		MutabilityStatus mutabilityStatus = getMutabilityStatus(response);
		if (response.hasStatus(HttpStatus.SC_OK)) {
			ModelResponse modelResponse = new ModelResponse(
					RDFParser.fromString(response.getBody().orElseThrow()).forceLang(treeNodeRequest.getLang())
							.toModel());
			return new TreeNodeResponse(modelResponse.getRelations(),
					modelResponse.getMembers(), mutabilityStatus);
		}
		if (response.hasStatus(HttpStatus.SC_MOVED_TEMPORARILY)) {
			return new TreeNodeResponse(
					List.of(response.getValueOfHeader(HttpHeaders.LOCATION).orElseThrow()),
					List.of(),
					new MutabilityStatus(false, LocalDateTime.MAX));
		}
		if (response.hasStatus(HttpStatus.SC_NOT_MODIFIED)) {
			return new TreeNodeResponse(List.of(), List.of(), mutabilityStatus);
		}
		throw new UnsupportedOperationException(
				"Cannot handle response " + response.getHttpStatus() + " of TreeNodeRequest " + treeNodeRequest);
	}

	private static MutabilityStatus getMutabilityStatus(Response response) {
		return response.getValueOfHeader(HttpHeaders.CACHE_CONTROL)
				.map(MutabilityStatus::ofHeader)
				.orElseGet(MutabilityStatus::empty);
	}
}
