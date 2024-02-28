package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioAmqpInRegistrator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.mock;

@Suite
@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@SuppressWarnings("java:S2187")
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class AmqpIntegrationTest {
	@Autowired
	ApplicationContext applicationContext;

	ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);

	public LdioAmqpInRegistrator jmsInRegistrator() {
		return new LdioAmqpInRegistrator(applicationContext);
	}
	public ApplicationEventPublisher applicationEventPublisher() {
		return applicationEventPublisher;
	}
}
