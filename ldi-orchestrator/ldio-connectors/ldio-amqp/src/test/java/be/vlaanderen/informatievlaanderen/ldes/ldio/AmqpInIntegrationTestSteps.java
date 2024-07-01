package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.AmqpConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioAmqpInAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioAmqpInRegistrator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import io.cucumber.java.After;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioAmqpIn.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.status.PipelineStatusTrigger.HALT;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.status.PipelineStatusTrigger.RESUME;
import static org.apache.jena.riot.RDFLanguages.contentTypeToLang;
import static org.apache.jena.riot.RDFLanguages.nameToLang;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AmqpInIntegrationTestSteps extends AmqpIntegrationTest {

	private final LdioAmqpInRegistrator ldioAmqpInRegistrator = jmsInRegistrator();
	private LdioInput ldioInput;
	private Model inputModel;
	private String contentType;
	private Map<String, String> config;
	private List<LdiAdapter.Content> adapterResult;
	private List<Model> componentExecutorResult;

	private final TestContext testContext = TestContextContainer.getTestContext();
	private MessageProducer producer;

	@After
	public void tearDown() {
		if(ldioInput != null) {
			ldioInput.shutdown();
		}
	}

	@And("I create a message producer")
	public void iCreateAMessageProducer() throws JMSException {
		producer = testContext.session.createProducer(testContext.queue);
	}

    @And("I prepare the result lists")
	public void iPrepareTheResultLists() {
		adapterResult = new ArrayList<>();
		componentExecutorResult = new ArrayList<>();
	}

	@And("I start a listener with an LdioJmsIn component")
	public void iCreateAnLdioJmsInComponent() {
		ComponentProperties properties = new ComponentProperties("pipelineName", NAME, config);
		final ComponentExecutor componentExecutor = linkedDataModel -> componentExecutorResult.add(linkedDataModel);
		final LdiAdapter adapter = content -> {
			adapterResult.add(content);
			return Stream.of(toModel(content));
		};
		var ldioJmsInConfigurator = new LdioAmqpInAutoConfig().ldioConfigurator(ldioAmqpInRegistrator, null);
		ldioInput = ldioJmsInConfigurator.configure(adapter, componentExecutor, applicationEventPublisher, properties);
	}

	@And("^I send a model from (.*) and (.*) to broker using the amqp producer")
	public void iSendAModelFromContentAndContentTypeToBrokerUsingTheAMQPProducer(String modelString, String contentType) throws JMSException {
		this.contentType = contentType;
		this.inputModel = RDFParser.fromString(modelString).lang(contentTypeToLang(contentType)).toModel();

		final String value = RDFWriter.source(inputModel).lang(contentTypeToLang(contentType)).build().asString();

		producer.send(testContext.session.createTextMessage(value));
	}

	private Model toModel(LdiAdapter.Content content) {
		return RDFParserBuilder.create()
				.fromString(content.content()).lang(nameToLang(content.mimeType())).toModel();
	}

	@And("^I create default config for LdioJmsIn with (.*)")
	public void iCreateDefaultConfigForLdioJmsInWithContentType(String contentType) throws JMSException {
		config = new HashMap<>();
		config.put(AmqpConfig.REMOTE_URL, "amqp://%s:%d".formatted(testContext.activemq.getHost(), testContext.activemq.getMappedPort(61616)));
		config.put(AmqpConfig.USERNAME, testContext.activemq.getUser());
		config.put(AmqpConfig.PASSWORD, testContext.activemq.getPassword());
		config.put(AmqpConfig.QUEUE, testContext.queue.getQueueName());
		config.put(AmqpConfig.CONTENT_TYPE, contentType);
		config.put(OrchestratorConfig.ORCHESTRATOR_NAME, "orchestratorName");
	}

	@Then("Wait for {int} messages")
	public void theListenerWillWaitForTheMessage(int i) {
		await().until(() -> adapterResult.size() == i);
	}

	@Then("Wait for a grace period")
	public void waitForGracePeriod() {
		Awaitility.waitAtMost(Duration.of(500, ChronoUnit.MILLIS));
	}

	@And("The result value will contain the model")
	public void theResultValueWillContainTheModel() {
		Model resultModel = RDFParser.fromString(adapterResult.getFirst().content()).lang(contentTypeToLang(contentType))
				.build().toModel();
		assertTrue(resultModel.isIsomorphicWith(inputModel));
	}

	@When("I pause the pipeline")
	public void pausePipeline() {
		ldioInput.updateStatus(HALT);
	}

	@When("I unpause the pipeline")
	public void unPausePipeline() {
		ldioInput.updateStatus(RESUME);
	}

	@And("The result value will not contain the model")
	public void theResultValueWillNotContainTheModel() {
		assertEquals(0, adapterResult.size());
	}

	@And("The componentExecutor will have been called {int} times")
	public void theComponentExecutorWillHaveBeenCalled(int interactions) {
		assertEquals(interactions, componentExecutorResult.size());
	}

	@ParameterType(value = "true|True|TRUE|false|False|FALSE")
	public Boolean booleanValue(String value) {
		return Boolean.valueOf(value);
	}

}
