package be.vlaanderen.informatievlaanderen.ldes.ldio;

import jakarta.jms.Queue;
import jakarta.jms.Session;
import org.testcontainers.activemq.ArtemisContainer;

public class TestContext {

    final ArtemisContainer activemq = new ArtemisContainer("apache/activemq-artemis:2.30.0-alpine")
            .withUser("user")
            .withPassword("password")
            .withExposedPorts(61616);

    Queue queue;
    Session session;
}
