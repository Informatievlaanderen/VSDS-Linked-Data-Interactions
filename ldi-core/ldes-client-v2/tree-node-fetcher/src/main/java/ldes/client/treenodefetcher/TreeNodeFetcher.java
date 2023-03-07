package ldes.client.treenodefetcher;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeader;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.requestexecutor.executor.RequestExecutorFactory;
import ldes.client.treenodefetcher.domain.entities.TreeNode;
import ldes.client.treenodefetcher.domain.valueobjects.ModelResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.util.List;

class TreeNodeFetcher {
	private final Lang dataSourceFormat = Lang.JSONLD;
	// TODO: 7/03/2023 wiring from config - support multiple executor strategies
	private final RequestExecutor requestExecutor = new RequestExecutorFactory().createNoAuthRequestExecutor();

	public TreeNode fetchFragment(String fragmentUrl) {
		RequestHeaders requestHeaders = new RequestHeaders(
				List.of(new RequestHeader(HttpHeaders.ACCEPT, dataSourceFormat.getHeaderString())));
		Response response = requestExecutor.apply(new Request(fragmentUrl, requestHeaders));
		if (response.getHttpStatus() == HttpStatus.SC_OK) {
			ModelResponse modelResponse = new ModelResponse(
					RDFParser.fromString(response.getBody().orElseThrow()).forceLang(dataSourceFormat).toModel());
			return new TreeNode(fragmentUrl, modelResponse.getRelations(), modelResponse.getMembers());
		}
		if (response.getHttpStatus() == HttpStatus.SC_MOVED_TEMPORARILY) {
			return new TreeNode(fragmentUrl, List.of(response.getValueOfHeader(HttpHeaders.LOCATION).orElseThrow()),
					List.of());
		}
		if (response.getHttpStatus() == HttpStatus.SC_NOT_MODIFIED) {
			return new TreeNode(fragmentUrl, List.of(), List.of());
		}
		throw new RuntimeException("a");
	}
}
