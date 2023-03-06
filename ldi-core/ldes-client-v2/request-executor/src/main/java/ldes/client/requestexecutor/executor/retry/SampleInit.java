package ldes.client.requestexecutor.executor.retry;

import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.requestexecutor.executor.RequestExecutorFactory;

// TODO: 6/03/2023 remove this
public class SampleInit {

	private final RequestExecutorFactory factory;

	public SampleInit(RequestExecutorFactory factory) {
		this.factory = factory;
	}

	void regularCode() {
		RequestExecutor noAuthRequestExecutor = factory.createNoAuthRequestExecutor();
		RequestExecutor retryExecutor = factory.createRetry(noAuthRequestExecutor);
	}

}
