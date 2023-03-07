package ldes.client.treenodefetcher.domain.valueobjects;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeader;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import org.apache.jena.riot.Lang;

import java.util.List;

public class TreeNodeRequest {
	private final String treeNodeUrl;
	private final Lang lang;

	public TreeNodeRequest(String treeNodeUrl, Lang lang) {
		this.treeNodeUrl = treeNodeUrl;
		this.lang = lang;
	}

	public Request createRequest() {
		RequestHeaders requestHeaders = new RequestHeaders(
				List.of(new RequestHeader("Accept", lang.getHeaderString())));
		return new Request(treeNodeUrl, requestHeaders);
	}

	public String getTreeNodeId() {
		return treeNodeUrl;
	}

	public Lang getLang() {
		return lang;
	}
}
