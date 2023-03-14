package ldes.client.requestexecutor.domain.valueobjects.executorsupplier;

import ldes.client.requestexecutor.executor.RequestExecutor;

public interface RequestExecutorSupplier {
	RequestExecutor createRequestExecutor();
}
