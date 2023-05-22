package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;

public interface RequestExecutorSupplier {
	RequestExecutor createRequestExecutor();
}
