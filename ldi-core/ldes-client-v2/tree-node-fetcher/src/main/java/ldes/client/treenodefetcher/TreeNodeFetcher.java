package ldes.client.treenodefetcher;

import ldes.client.requestexecutor.RequestProcessor;
import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeader;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.treenodefetcher.domain.entities.TreeNode;
import ldes.client.treenodefetcher.domain.valueobjects.ModelResponse;
import org.apache.http.HttpStatus;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.util.List;

class TreeNodeFetcher {
	private final Lang dataSourceFormat = Lang.JSONLD;
	private final RequestProcessor requestProcessor = new RequestProcessor();

	public TreeNode fetchFragment(String fragmentUrl) {
		RequestHeaders requestHeaders = new RequestHeaders(
				List.of(new RequestHeader("Accept", dataSourceFormat.getHeaderString())));
		Response response = requestProcessor
				.processRequest(new Request(fragmentUrl, requestHeaders));
		if (response.getHttpStatus() == HttpStatus.SC_OK) {
			ModelResponse modelResponse = new ModelResponse(
					RDFParser.fromString(response.getBody()).forceLang(dataSourceFormat).toModel());
			return new TreeNode(fragmentUrl, modelResponse.getRelations(), modelResponse.getMembers());
		}
		if (response.getHttpStatus() == HttpStatus.SC_MOVED_TEMPORARILY) {
			return new TreeNode(fragmentUrl, response.getValueOfHeader("location"), List.of());
		}
		if (response.getHttpStatus() == HttpStatus.SC_NOT_MODIFIED) {
			return new TreeNode(fragmentUrl, List.of(), List.of());
		}
		throw new RuntimeException("a");
	}
}
