package ldes.client.treenodefetcher.domain.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TreeNodeRequestTest {
	private static final String URL = "http://example.com";
	private static final Lang LANG = Lang.TTL;


	@Test
	void test_BasicCreate() {
		final TreeNodeRequest treeNodeRequest = new TreeNodeRequest(URL, LANG, "");

		final Request result = treeNodeRequest.createRequest();

		assertThat(result)
				.isInstanceOf(GetRequest.class)
				.extracting(Request::getUrl)
				.isEqualTo(URL);
	}

	@Test
	void given_EtagIsNull_when_CreateRequest_then_RequestContainsTwoHeaders() {
		final TreeNodeRequest treeNodeRequest = new TreeNodeRequest(URL, LANG, null);

		final Request result = treeNodeRequest.createRequest();

		assertThat(result.getRequestHeaders())
				.containsExactlyInAnyOrder(
						new RequestHeader("Accept", LANG.getHeaderString()),
						new RequestHeader("Accept-Encoding", "gzip")
				);
	}

	@Test
	void given_EtagIsNonNull_when_CreateRequest_then_RequestContainsThreeHeaders() {
		final String etag = "my-etag";
		final TreeNodeRequest treeNodeRequest = new TreeNodeRequest(URL, LANG, etag);

		final Request result = treeNodeRequest.createRequest();

		assertThat(result.getRequestHeaders())
				.containsExactlyInAnyOrder(
						new RequestHeader("Accept", LANG.getHeaderString()),
						new RequestHeader("If-None-Match", etag),
						new RequestHeader("Accept-Encoding", "gzip")
				);
	}
}