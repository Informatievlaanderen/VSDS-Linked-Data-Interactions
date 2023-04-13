package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor", value = "be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor")
public class RequestExecutorIT {
}
