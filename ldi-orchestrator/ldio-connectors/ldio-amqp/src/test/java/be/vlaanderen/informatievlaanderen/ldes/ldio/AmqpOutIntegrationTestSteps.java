package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.AmqpConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioAmqpOutAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParser;

import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioAmqpOut.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.AmqpConfig.CONTENT_TYPE_HEADER;
import static org.apache.jena.riot.RDFLanguages.contentTypeToLang;
import static org.assertj.core.api.Assertions.assertThat;

public class AmqpOutIntegrationTestSteps {

    private final TestContext testContext = TestContextContainer.getTestContext();

    private Model inputModel;
    private Map<String, String> config;
    private Message message;
    private LdiOutput ldioAmqpOut;
    private Lang contentType;
    private MessageConsumer consumer;

    @And("I create a message consumer")
    public void iCreateAMessageConsumer() throws JMSException {
        consumer = testContext.session.createConsumer(testContext.queue);
    }

    @And("I create an LdioAmqpOut component")
    public void iCreateAnLdioAmqpOutComponent() {
        ComponentProperties properties = new ComponentProperties("pipelineName", NAME, config);
        ldioAmqpOut = (LdiOutput) new LdioAmqpOutAutoConfig().ldioConfigurator().configure(properties);
    }

    @And("^I create a model with (.*) and (.*)")
    public void iCreateAModelWithContentTypeAnd(String contentType, String content) {
        this.contentType = RDFLanguages.contentTypeToLang(contentType);
        this.inputModel = RDFParser.fromString(content).lang(contentTypeToLang(contentType)).toModel();
    }

    @And("I create default config for LdioAmqpOut")
    public void iCreateDefaultConfigForLdioAmqpOut() throws JMSException {
        config = new HashMap<>();
        config.put(AmqpConfig.REMOTE_URL, "amqp://%s:%d".formatted(testContext.activemq.getHost(), testContext.activemq.getMappedPort(61616)));
        config.put(AmqpConfig.USERNAME, testContext.activemq.getUser());
        config.put(AmqpConfig.PASSWORD, testContext.activemq.getPassword());
        config.put(AmqpConfig.QUEUE, testContext.queue.getQueueName());
        config.put("rdf-writer." + LdiRdfWriterProperties.CONTENT_TYPE, contentType.getHeaderString());
    }

    @Then("I send the model to the LdioAmqpOut component")
    public void iCanSendTheModelToTheLdioAmqpOutComponent() {
        ldioAmqpOut.accept(inputModel);
    }

    @Then("The mock listener will wait for the message")
    public void theListenerWillWaitForTheMessage() throws JMSException {
        message = consumer.receive();
    }

    @And("The mock listener result will contain the content-type")
    public void theResultHeaderWillContainTheContentType() throws JMSException {
        assertThat(contentType.getHeaderString()).isEqualTo(message.getStringProperty(CONTENT_TYPE_HEADER));
    }

    @And("The mock listener result will contain the model")
    public void theResultValueWillContainTheModel() throws JMSException {
        String body = message.getBody(String.class);
        Model resultModel = RDFParser.fromString(body).lang(contentType).build().toModel();
        assertThat(resultModel.isIsomorphicWith(inputModel)).isTrue();
    }
}
