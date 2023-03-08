package ldes.client.treenodefetcher;

import ldes.client.requestexecutor.domain.valueobjects.DefaultConfig;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.domain.entities.TreeNode;
import ldes.client.treenodefetcher.domain.valueobjects.ModelResponse;
import ldes.client.treenodefetcher.domain.valueobjects.MutabilityStatus;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.jena.riot.RDFParser;

import java.time.LocalDateTime;
import java.util.List;

public class TreeNodeFetcher {
	// TODO: 7/03/2023 wiring from config - support multiple executor strategies
	private final RequestExecutor requestExecutor = new DefaultConfig().createRequestExecutor();

	public TreeNode fetchTreeNode(TreeNodeRequest treeNodeRequest) {
		Response response = requestExecutor
				.execute(treeNodeRequest.createRequest());
		MutabilityStatus mutabilityStatus = MutabilityStatus.ofHeader(response.getValueOfHeader(HttpHeaders.CACHE_CONTROL));
		if (response.getHttpStatus() == HttpStatus.SC_OK) {
			ModelResponse modelResponse = new ModelResponse(
					RDFParser.fromString(response.getBody().orElseThrow()).forceLang(treeNodeRequest.getLang()).toModel());
			return new TreeNode(treeNodeRequest.getTreeNodeId(), modelResponse.getRelations(),
					modelResponse.getMembers(), mutabilityStatus);
		}
		if (response.getHttpStatus() == HttpStatus.SC_MOVED_TEMPORARILY) {
			return new TreeNode(treeNodeRequest.getTreeNodeId(), List.of(response.getValueOfHeader(HttpHeaders.LOCATION).orElseThrow()),
					List.of(),
					new MutabilityStatus(false, LocalDateTime.MAX));
		}
		if (response.getHttpStatus() == HttpStatus.SC_NOT_MODIFIED) {
			return new TreeNode(treeNodeRequest.getTreeNodeId(), List.of(), List.of(), mutabilityStatus);
		}
		// to do proper exception
		throw new UnsupportedOperationException("a");
	}
}
