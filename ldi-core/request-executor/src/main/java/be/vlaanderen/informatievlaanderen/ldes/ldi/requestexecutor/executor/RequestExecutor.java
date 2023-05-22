package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;

public interface RequestExecutor {

	Response execute(Request request);

}
