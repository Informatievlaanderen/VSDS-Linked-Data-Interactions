package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SuppressWarnings("java:S2187")
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class KafkaIntegrationTest {
}
