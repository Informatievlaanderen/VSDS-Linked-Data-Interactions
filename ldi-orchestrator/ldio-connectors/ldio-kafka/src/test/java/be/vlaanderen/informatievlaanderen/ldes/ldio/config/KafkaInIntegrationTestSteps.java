package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.status.PipelineStatusTrigger;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaCommonIntegrationSteps.bootstrapServer;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaCommonIntegrationSteps.topic;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.CONTENT_TYPE;
import static org.apache.jena.riot.RDFLanguages.contentTypeToLang;
import static org.apache.jena.riot.RDFLanguages.nameToLang;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KafkaInIntegrationTestSteps extends KafkaIntegrationTest {
	private final ApplicationEventPublisher applicationEventPublisher = applicationEventPublisher();
	private Model inputModel;

	private String contentType;
	private Map<String, String> config;
	private List<LdiAdapter.Content> adapterResult;
	private List<Model> componentExecutorResult;
	private KafkaTemplate<String, String> kafkaProducer;
	private LdioInput kafkaIn;

	@And("I prepare the result lists")
	public void iPrepareTheResultLists() {
		adapterResult = new ArrayList<>();
		componentExecutorResult = new ArrayList<>();
	}

	@And("I start a listener with an LdioKafkaIn component")
	public void iCreateAnLdioKafkaInComponent() {
		ComponentProperties properties = new ComponentProperties("pipelineName", NAME, config);
		final ComponentExecutor componentExecutor = linkedDataModel -> componentExecutorResult.add(linkedDataModel);
		final LdiAdapter adapter = content -> {
			adapterResult.add(content);
			return Stream.of(toModel(content));
		};
		var ldioKafkaInConfigurator = new LdioKafkaInAutoConfig().ldioConfigurator(null);
		kafkaIn = ldioKafkaInConfigurator.configure(adapter, componentExecutor, applicationEventPublisher, properties);
	}

	private Model toModel(LdiAdapter.Content content) {
		return RDFParserBuilder.create()
				.fromString(content.content()).lang(nameToLang(content.mimeType())).toModel();
	}

	@And("^I create a model from (.*) and (.*)$")
	public void iCreateAModel(String modelString, String contentType) {
		this.contentType = contentType;
		this.inputModel = RDFParser.fromString(modelString).lang(contentTypeToLang(contentType)).toModel();
	}

	@And("I create default config for LdioKafkaIn")
	public void iCreateDefaultConfigForLdioKafkaIn() {
		config = new HashMap<>();
		config.put(KafkaInConfigKeys.BOOTSTRAP_SERVERS, bootstrapServer);
		config.put(KafkaInConfigKeys.TOPICS, topic);
		config.put(OrchestratorConfig.ORCHESTRATOR_NAME, "orchestratorName");
		config.put(PipelineConfig.PIPELINE_NAME, "pipelineName");
	}

	@And("I create a kafka producer")
	public void iCreateAKafkaProducer() {
		final var configProps = new HashMap<String, Object>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		kafkaProducer = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
	}

	@And("^I send the model to broker using the kafka producer")
	public void iSendTheModelToTheUsingTheKafkaProducer() {
		final String value = RDFWriter.source(inputModel).lang(contentTypeToLang(contentType)).build().asString();
		final var headers = new RecordHeaders().add(CONTENT_TYPE, contentType.getBytes());
		ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, null, (String) null, value,
				headers);
		kafkaProducer.send(producerRecord);
	}

	@Then("Wait for the message")
	public void theListenerWillWaitForTheMessage() {
		await().until(() -> adapterResult.size() == 1);
	}

	@Then("Wait for a grace period")
	public void theListenerWillWaitForPeriod() {
		Awaitility.waitAtMost(1500, TimeUnit.MILLISECONDS);
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

	@And("The componentExecutor will have been called {int} times")
	public void theComponentExecutorWillHaveBeenCalled(int i) {
		assertEquals(i, componentExecutorResult.size());
	}

	@When("I pause the pipeline")
	public void pauseInput() {
		kafkaIn.updateStatus(PipelineStatusTrigger.HALT);
	}

	@When("I unpause the pipeline")
	public void unPauseInput() {
		kafkaIn.updateStatus(PipelineStatusTrigger.RESUME);
	}

}
