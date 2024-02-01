package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.AmqpInConfigKeys;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import org.testcontainers.activemq.ArtemisContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.apache.jena.riot.RDFLanguages.contentTypeToLang;
import static org.apache.jena.riot.RDFLanguages.nameToLang;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AmqpInIntegrationTestSteps extends AmqpIntegrationTest {
	public LdioAmqpInRegistrator ldioAmqpInRegistrator = jmsInRegistrator();
	ArtemisContainer activemq = new ArtemisContainer("apache/activemq-artemis:2.30.0-alpine")
			.withUser("user")
			.withPassword("password")
			.withExposedPorts(61616);
	private Model inputModel;
	private String topic;
	private String contentType;
	private Map<String, String> config;
	private List<LdiAdapter.Content> adapterResult;
	private List<Model> componentExecutorResult;
	private Session session;
	private Connection connection;
	private MessageProducer producer;

	@Given("^I create a topic for my scenario: (.*)$")
	public void iCreateATopic(String topic) throws JMSException {
		this.topic = topic;
		activemq.start();

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activemq.getBrokerUrl());
		connection = connectionFactory.createConnection(activemq.getUser(), activemq.getPassword());
		connection.start();

		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Getting the queue
		Queue queue = session.createQueue(topic);

		// Creating the producer & consumer
		producer = session.createProducer(queue);
	}

	@And("I prepare the result lists")
	public void iPrepareTheResultLists() {
		adapterResult = new ArrayList<>();
		componentExecutorResult = new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	@And("I start a listener with an LdioJmsIn component")
	public void iCreateAnLdioKafkaInComponent() {
		ComponentProperties properties = new ComponentProperties(config);
		final ComponentExecutor componentExecutor = linkedDataModel -> componentExecutorResult.add(linkedDataModel);
		final LdiAdapter adapter = content -> {
			adapterResult.add(content);
			return Stream.of(toModel(content));
		};
		var ldioJmsInConfigurator = new LdioAmqpInAutoConfig().ldioConfigurator(ldioAmqpInRegistrator, null);
		ldioJmsInConfigurator.configure(adapter, componentExecutor, properties);
	}

	@And("^I send a model from (.*) and (.*) to broker using the kafka producer")
	public void iSendAModelFromContentAndContentTypeToBrokerUsingTheKafkaProducer(String modelString, String contentType) throws JMSException {
		this.contentType = contentType;
		this.inputModel = RDFParser.fromString(modelString).lang(contentTypeToLang(contentType)).toModel();

		final String value = RDFWriter.source(inputModel).lang(contentTypeToLang(contentType)).build().asString();

		producer.send(session.createTextMessage(value));
	}


	private Model toModel(LdiAdapter.Content content) {
		return RDFParserBuilder.create()
				.fromString(content.content()).lang(nameToLang(content.mimeType())).toModel();
	}

	@And("^I create default config for LdioJmsIn with (.*)")
	public void iCreateDefaultConfigForLdioJmsInWithContentType(String contentType) {
		config = new HashMap<>();
		config.put(AmqpInConfigKeys.REMOTE_URL, "amqp://%s:%d".formatted(activemq.getHost(), activemq.getMappedPort(61616)));
		config.put(AmqpInConfigKeys.USERNAME, activemq.getUser());
		config.put(AmqpInConfigKeys.PASSWORD, activemq.getPassword());
		config.put(AmqpInConfigKeys.TOPIC, topic);
		config.put(AmqpInConfigKeys.CONTENT_TYPE, contentType);
		config.put(OrchestratorConfig.ORCHESTRATOR_NAME, "orchestratorName");
		config.put(PipelineConfig.PIPELINE_NAME, "pipelineName");
	}

	@Then("Wait for the message")
	public void theListenerWillWaitForTheMessage() {
		await().until(() -> adapterResult.size() == 1);
	}

	@And("^The result header will contain the (.*)$")
	public void theResultHeaderWillContainTheContentType(String expectedContentType) {
		assertEquals(expectedContentType, adapterResult.get(0).mimeType());
	}

	@And("The result value will contain the model")
	public void theResultValueWillContainTheModel() {
		Model resultModel = RDFParser.fromString(adapterResult.get(0).content()).lang(contentTypeToLang(contentType))
				.build().toModel();
		assertTrue(resultModel.isIsomorphicWith(inputModel));
	}

	@And("The componentExecutor will have been called")
	public void theComponentExecutorWillHaveBeenCalled() {
		assertEquals(1, componentExecutorResult.size());
	}

}
