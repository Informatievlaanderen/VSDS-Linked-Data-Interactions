package ldes.client.treenodefetcher;

import ldes.client.requestexecutor.RequestProcessor;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.treenodefetcher.domain.entities.TreeNode;
import ldes.client.treenodefetcher.domain.valueobjects.ModelResponse;
import ldes.client.treenodefetcher.domain.valueobjects.MutabilityStatus;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import org.apache.http.HttpStatus;
import org.apache.jena.riot.RDFParser;

import java.time.LocalDateTime;
import java.util.List;

public class TreeNodeFetcher {
	private final RequestProcessor requestProcessor = new RequestProcessor();

	public TreeNode fetchTreeNode(TreeNodeRequest treeNodeRequest) {
		Response response = requestProcessor
				.processRequest(treeNodeRequest.createRequest());
		MutabilityStatus mutabilityStatus = MutabilityStatus.ofHeader(response.getValueOfHeader("cache-control"));
		if (response.getHttpStatus() == HttpStatus.SC_OK) {
			ModelResponse modelResponse = new ModelResponse(
					RDFParser.fromString(response.getBody()).forceLang(treeNodeRequest.getLang()).toModel());
			return new TreeNode(treeNodeRequest.getTreeNodeId(), modelResponse.getRelations(),
					modelResponse.getMembers(), mutabilityStatus);
		}
		if (response.getHttpStatus() == HttpStatus.SC_MOVED_TEMPORARILY) {
			return new TreeNode(treeNodeRequest.getTreeNodeId(), response.getValueOfHeader("location"), List.of(),
					new MutabilityStatus(false, LocalDateTime.MAX));
		}
		if (response.getHttpStatus() == HttpStatus.SC_NOT_MODIFIED) {
			return new TreeNode(treeNodeRequest.getTreeNodeId(), List.of(), List.of(), mutabilityStatus);
		}
		// to do proper exception
		throw new UnsupportedOperationException("a");
	}
}
