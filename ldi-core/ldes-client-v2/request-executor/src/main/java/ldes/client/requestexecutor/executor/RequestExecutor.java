package ldes.client.requestexecutor.executor;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.Response;

public interface RequestExecutor {

	Response execute(Request request);

}
