package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor;

/**
 * Interface to supply a RequestExecutor
 * <br/>
 * Will typically be implemented by some config class that will create a RequestExecutor based on that class level config
 */
public interface RequestExecutorSupplier {
	/**
	 * @return instance of RequestExecutor, based on class based configuration
	 */
	RequestExecutor createRequestExecutor();
}
