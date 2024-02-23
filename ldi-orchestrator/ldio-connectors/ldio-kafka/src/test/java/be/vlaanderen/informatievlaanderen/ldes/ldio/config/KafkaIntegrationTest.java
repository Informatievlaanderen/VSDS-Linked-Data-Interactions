package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@Suite
@SuppressWarnings("java:S2187")
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class KafkaIntegrationTest {
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    public ApplicationEventPublisher applicationEventPublisher() {
        return applicationEventPublisher;
    }
}
