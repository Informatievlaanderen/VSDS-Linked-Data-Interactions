package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaIntegrationSteps.bootstrapServer;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaIntegrationSteps.topic;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KafkaOutIntegrationTestSteps {

	private LdiOutput ldioKafkaOut;
	private Model inputModel;
	private Map<String, String> config;
	private List<ConsumerRecord<String, String>> result;
	private static final Lang contentType = Lang.TURTLE;

	@And("I create an LdioKafkaOut component")
	public void iCreateAnLdioKafkaOutComponent() {
		ComponentProperties properties = new ComponentProperties(config);
		ldioKafkaOut = (LdiOutput) new LdioKafkaOutAutoConfig().ldiKafkaOutConfigurator().configure(properties);
	}

	@And("I create a model")
	public void iCreateAModel() {
		inputModel = ModelFactory.createDefaultModel();
		inputModel.add(createResource("http://data-from-source/"), createProperty("http://test/"), "Data!");
	}

	@And("I create default config for LdioKafkaOut")
	public void iCreateDefaultConfigForLdioKafkaOut() {
		config = new HashMap<>();
		config.put(KafkaOutConfigKeys.CONTENT_TYPE, contentType.getHeaderString());
		config.put(KafkaOutConfigKeys.BOOTSTRAP_SERVERS, bootstrapServer);
		config.put(KafkaOutConfigKeys.TOPIC, topic);
	}

	@And("I configure a key property path {string}")
	public void iConfigureAKeyPropertyPath(String propertyPath) {
		config.put(KafkaOutConfigKeys.KEY_PROPERTY_PATH, propertyPath);
	}

	@And("I start a kafka listener")
	public void iCreateAKafkaListener() {
		final Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		var consumerFactory = new DefaultKafkaConsumerFactory<>(props);
		ContainerProperties containerProps = new ContainerProperties(topic);
		result = new ArrayList<>();
		containerProps.setMessageListener((MessageListener<String, String>) x -> result.add(x));
		new KafkaMessageListenerContainer<>(consumerFactory, containerProps).start();
	}

	@Then("I send the model to the LdioKafkaOut component")
	public void iCanSendTheModelToTheLdioKafkaOutComponent() {
		ldioKafkaOut.accept(inputModel);
	}

	@Then("The listener will wait for the message")
	public void theListenerWillWaitForTheMessage() {
		await().until(() -> result.size() == 1);
	}

	@And("The mock listener result header will contain the content-type")
	public void theResultHeaderWillContainTheContentType() {
		result.get(0).headers().headers(KafkaOutConfigKeys.CONTENT_TYPE)
				.forEach(header -> assertEquals(contentType.getHeaderString(), new String(header.value())));
	}

	@And("The mock listener result value will contain the model")
	public void theResultValueWillContainTheModel() {
		Model resultModel = RDFParser.fromString(result.get(0).value()).lang(contentType).build().toModel();
		assertTrue(resultModel.isIsomorphicWith(inputModel));
	}

	@And("I add an n-quad to the model: {string}")
	public void iAddATripleToTheModel(String triple) {
		inputModel.add(RDFParser.fromString(triple).lang(Lang.NQUADS).toModel());
	}

	@And("The result key will be {string}")
	public void theResultKeyWillBe(String expectedKey) {
		assertEquals(expectedKey, result.get(0).key());
	}
}
