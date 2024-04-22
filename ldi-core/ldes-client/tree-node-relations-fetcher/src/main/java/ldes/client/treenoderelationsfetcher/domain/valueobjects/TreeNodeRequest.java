package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import org.apache.http.HttpHeaders;
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
				List.of(new RequestHeader(HttpHeaders.ACCEPT, lang.getHeaderString())));

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
				'}';
	}
}
