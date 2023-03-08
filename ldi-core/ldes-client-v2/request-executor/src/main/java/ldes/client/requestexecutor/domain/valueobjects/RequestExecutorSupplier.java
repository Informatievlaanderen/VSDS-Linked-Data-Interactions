package ldes.client.requestexecutor.domain.valueobjects;

import ldes.client.requestexecutor.executor.RequestExecutor;

public interface RequestExecutorSupplier {
    RequestExecutor createRequestExecutor();
}
