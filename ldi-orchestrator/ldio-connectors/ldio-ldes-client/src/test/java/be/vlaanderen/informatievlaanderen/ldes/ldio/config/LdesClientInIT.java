package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@Suite
@SuppressWarnings("java:S2187")
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@WireMockTest(httpPort = 10101)
public class LdesClientInIT {
}
