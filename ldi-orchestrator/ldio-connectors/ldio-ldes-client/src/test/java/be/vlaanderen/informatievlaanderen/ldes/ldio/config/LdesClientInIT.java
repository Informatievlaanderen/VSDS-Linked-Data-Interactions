package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static org.mockito.Mockito.mock;

@Suite
@SuppressWarnings("java:S2187")
@IncludeEngines("cucumber")
@SpringBootTest
@SelectClasspathResource("features")
@WireMockTest(httpPort = 10101)
public class LdesClientInIT {
    private ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    public ApplicationEventPublisher applicationEventPublisher() {
        return eventPublisher;
    }

}
