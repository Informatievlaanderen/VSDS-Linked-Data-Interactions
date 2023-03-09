package ldes.client.requestexecutor.executor;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "ldes.client.requestexecutor", value = "ldes.client.requestexecutor")
public class RequestExecutorIT {
}
