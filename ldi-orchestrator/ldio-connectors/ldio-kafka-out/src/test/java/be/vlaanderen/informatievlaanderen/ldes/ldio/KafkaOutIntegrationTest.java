package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "be.vlaanderen.informatievlaanderen.ldes.ldio", value = "be.vlaanderen.informatievlaanderen.ldes.ldio")
public class KafkaOutIntegrationTest {
}
