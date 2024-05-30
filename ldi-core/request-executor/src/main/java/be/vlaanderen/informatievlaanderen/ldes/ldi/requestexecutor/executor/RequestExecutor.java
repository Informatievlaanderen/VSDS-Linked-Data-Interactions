package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;

/**
 * Custom interface to make more manageable to deal with HttpRequests and HttpResponses
 */
public interface RequestExecutor {

	Response execute(Request request);

}
