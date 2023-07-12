package ldes.client.startingtreenode;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import ldes.client.startingtreenode.domain.valueobjects.StartingNodeRequest;
import ldes.client.startingtreenode.exception.StartingNodeNotFoundException;
import org.apache.http.HttpHeaders;

import java.util.List;

public class RedirectRequestExecutor {

	private final RequestExecutor requestExecutor;

	public RedirectRequestExecutor(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	/**
	 * Executes the request. Will follow redirects until a success response is
	 * obtained.
	 */
	public Response execute(final StartingNodeRequest startingNodeRequest) {
		RequestHeaders requestHeaders = new RequestHeaders(
				List.of(new RequestHeader(HttpHeaders.ACCEPT, startingNodeRequest.contentType())));
		Response response = requestExecutor.execute(new Request(startingNodeRequest.url(), requestHeaders));
		if (response.isOk()) {
			return response;
		}
		if (response.isRedirect()) {
			StartingNodeRequest newStartingNodeRequest = startingNodeRequest
					.createRedirectedEndpoint(response.getRedirectLocation()
							.orElseThrow(() -> new StartingNodeNotFoundException(startingNodeRequest.url(),
									"No Location Header in redirect.")));
			return execute(newStartingNodeRequest);
		}
		throw new StartingNodeNotFoundException(startingNodeRequest.url(),
				"Unable to hande response " + response.getHttpStatus());
	}

}
