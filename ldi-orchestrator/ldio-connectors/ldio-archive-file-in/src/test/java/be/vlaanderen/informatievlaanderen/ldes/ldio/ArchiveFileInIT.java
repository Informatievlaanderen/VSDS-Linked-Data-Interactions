package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.mock;

@Suite
@SuppressWarnings("java:S2187")
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class ArchiveFileInIT {
    ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
    public ApplicationEventPublisher applicationEventPublisher() {
        return applicationEventPublisher;
    }
}
