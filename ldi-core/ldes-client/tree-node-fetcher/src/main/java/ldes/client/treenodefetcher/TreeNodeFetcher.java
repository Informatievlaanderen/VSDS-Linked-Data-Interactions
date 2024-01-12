package ldes.client.treenodefetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromCurrentTimeExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromPathExtractor;
import ldes.client.treenodefetcher.domain.valueobjects.ModelResponse;
import ldes.client.treenodefetcher.domain.valueobjects.MutabilityStatus;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeResponse;
import org.apache.http.HttpHeaders;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import static ldes.client.treenodefetcher.domain.valueobjects.Constants.ANY_RESOURCE;
import static ldes.client.treenodefetcher.domain.valueobjects.Constants.W3ID_LDES_TIMESTAMP_PATH;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class TreeNodeFetcher {

	private final RequestExecutor requestExecutor;
	private TimestampExtractor timestampExtractor;

	public TreeNodeFetcher(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	public TreeNodeResponse fetchTreeNode(TreeNodeRequest treeNodeRequest) {
		final Response response = requestExecutor.execute(treeNodeRequest.createRequest());

		if (response.isOk()) {
			return createOkResponse(treeNodeRequest, response);
		}

		if (response.isRedirect()) {
			return createRedirectResponse(response);
		}

		if (response.isNotModified()) {
			return createNotModifiedResponse(response);
		}

		throw new UnsupportedOperationException(
				"Cannot handle response " + response.getHttpStatus() + " of TreeNodeRequest " + treeNodeRequest);
	}

	private TreeNodeResponse createOkResponse(TreeNodeRequest treeNodeRequest, Response response) {
		final InputStream responseBody = response.getBody().map(ByteArrayInputStream::new).orElseThrow();
		final Model model = RDFParser.source(responseBody).forceLang(treeNodeRequest.getLang()).base(treeNodeRequest.getTreeNodeUrl()).toModel();
		initialiseTimestampExtractor(model);
		final ModelResponse modelResponse = new ModelResponse(model, timestampExtractor);
		final MutabilityStatus mutabilityStatus = getMutabilityStatus(response);
		return new TreeNodeResponse(modelResponse.getRelations(), modelResponse.getMembers(), mutabilityStatus);
	}

	private static TreeNodeResponse createRedirectResponse(Response response) {
		return new TreeNodeResponse(
				List.of(response.getRedirectLocation()
						.orElseThrow(() -> new IllegalStateException("No Location Header in redirect."))),
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

	private void initialiseTimestampExtractor(Model model) {
		if (timestampExtractor != null) {
			return;
		}
		model.listStatements(ANY_RESOURCE, W3ID_LDES_TIMESTAMP_PATH, ANY_RESOURCE)
				.nextOptional()
				.ifPresentOrElse(timestampPathStatement -> this.timestampExtractor = new TimestampFromPathExtractor(createProperty(String.valueOf(timestampPathStatement.getObject()))),
						() -> this.timestampExtractor = new TimestampFromCurrentTimeExtractor());
	}
}
