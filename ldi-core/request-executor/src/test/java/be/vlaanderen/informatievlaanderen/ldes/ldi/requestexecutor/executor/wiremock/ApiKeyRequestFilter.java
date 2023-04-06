package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.wiremock;

import com.github.tomakehurst.wiremock.extension.requestfilter.RequestFilterAction;
import com.github.tomakehurst.wiremock.extension.requestfilter.StubRequestFilter;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

public class ApiKeyRequestFilter extends StubRequestFilter {

	@Override
	public RequestFilterAction filter(Request request) {
		if (!request.getUrl().equals("/200-response-with-api-key")) {
			return RequestFilterAction.continueWith(request);
		}
		if (request.header("X-API-KEY").firstValue().equals("test123")) {
			return RequestFilterAction.continueWith(request);
		}

		return RequestFilterAction.stopWith(ResponseDefinition.notAuthorised());
	}

	@Override
	public String getName() {
		return "api-key";
	}
}
