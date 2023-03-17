package ldes.client.startingtreenode;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "ldes.client.startingtreenode", value = "ldes.client.startingtreenode")
public class StartingStartingTreeNodeFinderIT {
}
