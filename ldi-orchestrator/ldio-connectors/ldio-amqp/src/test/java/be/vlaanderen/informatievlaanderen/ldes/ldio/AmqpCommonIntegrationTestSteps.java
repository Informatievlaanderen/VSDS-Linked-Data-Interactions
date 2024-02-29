package be.vlaanderen.informatievlaanderen.ldes.ldio;

import io.cucumber.java.en.Given;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;

public class AmqpCommonIntegrationTestSteps {

    private final TestContext testContext = TestContextContainer.getTestContext();

    @Given("^I create a queue for my scenario: (.*)$")
    public void iCreateAQueue(String queueName) throws JMSException {
        testContext.activemq.start();

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(testContext.activemq.getBrokerUrl());
        Connection connection = connectionFactory.createConnection(testContext.activemq.getUser(), testContext.activemq.getPassword());
        connection.start();

        testContext.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        testContext.queue = testContext.session.createQueue(queueName);
    }

}
