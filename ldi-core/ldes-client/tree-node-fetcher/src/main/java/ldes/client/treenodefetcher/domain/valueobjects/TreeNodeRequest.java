package ldes.client.treenodefetcher.domain.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import org.apache.http.HttpHeaders;
import org.apache.jena.riot.Lang;

import java.util.List;

/**
 * Contains the endpoint to connect to the server. This can only be a fragment
 */
public class TreeNodeRequest {
	private final String treeNodeUrl;
	private final Lang lang;

	private final String etag;

	public TreeNodeRequest(String treeNodeUrl, Lang lang, String etag) {
		this.treeNodeUrl = treeNodeUrl;
		this.lang = lang;
		this.etag = etag;
	}

	public Request createRequest() {
		RequestHeaders requestHeaders = new RequestHeaders(
				List.of(new RequestHeader(HttpHeaders.ACCEPT, lang.getHeaderString())));
		if (etag != null) {
			requestHeaders = requestHeaders.withRequestHeader(new RequestHeader(HttpHeaders.IF_NONE_MATCH, etag));
		}
		return new GetRequest(treeNodeUrl, requestHeaders);
	}

	public Lang getLang() {
		return lang;
	}

	public String getTreeNodeUrl() {
		return treeNodeUrl;
	}

	@Override
	public String toString() {
		return "TreeNodeRequest{" +
				"treeNodeUrl='" + treeNodeUrl + '\'' +
				", lang=" + lang +
				", etag='" + etag + '\'' +
				'}';
	}
}
