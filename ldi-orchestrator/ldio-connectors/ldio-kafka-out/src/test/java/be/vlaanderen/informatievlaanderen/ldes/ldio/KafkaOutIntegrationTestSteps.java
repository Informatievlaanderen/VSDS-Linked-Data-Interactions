package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioKafkaOutAutoConfig;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KafkaOutIntegrationTestSteps {

	private LdiOutput ldioKafkaOut;
	private Model inputModel;

	private final List<ConsumerRecord<String, String>> result = new ArrayList<>();

	private static final String bootstrapServer = "localhost:9095";
	private final Lang contentType = Lang.TURTLE;

	@BeforeEach
	void setUp() {
		result.clear();
	}

	@Given("I start a kafka broker with topic {string}")
	public void iHaveAKafkaBroker(String topic) {
		EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1, true, 1);
		embeddedKafkaBroker
				.brokerProperty("listeners", "PLAINTEXT://%s".formatted(bootstrapServer))
				.brokerProperty("port", 9095);
		embeddedKafkaBroker.afterPropertiesSet();
		embeddedKafkaBroker.addTopics(topic);
	}

	@And("I create an LdioKafkaOut component for topic {string}")
	public void iCreateAnLdioKafkaOutComponent(String topic) {
		final Map<String, String> config = new HashMap<>();
		config.put(KafkaOutConfigKeys.CONTENT_TYPE, contentType.getHeaderString());
		config.put(KafkaOutConfigKeys.BOOTSTRAP_SERVERS, bootstrapServer);
		config.put(KafkaOutConfigKeys.TOPIC, topic);
		ComponentProperties properties = new ComponentProperties(config);
		ldioKafkaOut = (LdiOutput) new LdioKafkaOutAutoConfig().ldiKafkaOutConfigurator().configure(properties);
	}

	@And("I create a model")
	public void iCreateAModel() {
		inputModel = ModelFactory.createDefaultModel();
		inputModel.add(createResource("http://data-from-source/"), createProperty("http://test/"), "Data!");
	}

	@And("I start a kafka listener for topic {string}")
	public void iCreateAKafkaListener(String topic) {
		var consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerConfig());
		ContainerProperties containerProps = new ContainerProperties(topic);
		containerProps.setMessageListener((MessageListener<String, String>) data -> result.add(data));
		new KafkaMessageListenerContainer<>(consumerFactory, containerProps).start();
	}

	private Map<String, Object> getConsumerConfig() {
		final Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		return props;
	}

	@Then("I send the model to the LdioKafkaOut component")
	public void iCanSendTheModelToTheLdioKafkaOutComponent() {
		ldioKafkaOut.accept(inputModel);
	}


	@Then("The listener will wait for the message")
	public void theListenerWillWaitForTheMessage() {
		await().until(() -> result.size() == 1);
	}

	@And("The result header will contain the content-type")
	public void theResultHeaderWillContainTheContentType() {
		result.get(0).headers().headers(KafkaOutConfigKeys.CONTENT_TYPE).forEach(header ->
				assertEquals(contentType.getHeaderString(), new String(header.value())));
	}

	@And("The result value will contain the model")
	public void theResultValueWillContainTheModel() {
		Model resultModel = RDFParser.fromString(result.get(0).value()).lang(contentType).build().toModel();
		assertTrue(resultModel.isIsomorphicWith(inputModel));
	}
}
