package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioKafkaOutAutoConfig;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@EmbeddedKafka(brokerProperties = { "listeners=PLAINTEXT://localhost:9095", "port=9095" })
public class KafkaOutIntegrationTestSteps {

	private EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1, true, 1);

	final LdioKafkaOutAutoConfig autoConfig = new LdioKafkaOutAutoConfig();

	private final String topic = "embedded-test-topic";
	private final String bootstrapServer = "localhost:9095";
	private final String kafkaOutContentType = "text/turtle";

	@Given("I have a config")
	public void iHaveAConfig(DataTable data) {
		// data.asList().
		System.out.println(data);
	}

	@Then("I woo")
	public void iWoo() {
	}

	@Given("I have content-type {string}")
	public void iHaveContentType(String contentType) {

	}

	@And("topic {string}")
	public void topic(String topic) {

	}

	@When("I run")
	public void iRun() {
		// var kafkaListener = createKafkaListener();
		// kafkaListener.start();
		embeddedKafkaBroker.kafkaPorts(9095);
		embeddedKafkaBroker.afterPropertiesSet();
		embeddedKafkaBroker.addTopics(topic);

		// embeddedKafkaBroker.

		var model = createModelWithStatement();

		var output = createKafkaOutput();
		output.accept(model);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		// await().until(() -> result.size() == 1);
		// assertTrue(model.isIsomorphicWith(toModel(result.get(0))));
		// assertEquals(kafkaOutContentType, result.get(0).mimeType());
	}

	private LdiOutput createKafkaOutput() {
		final Map<String, String> config = new HashMap<>();
		config.put(KafkaOutConfigKeys.CONTENT_TYPE, kafkaOutContentType);
		config.put(KafkaOutConfigKeys.BOOTSTRAP_SERVERS, bootstrapServer);
		config.put(KafkaOutConfigKeys.TOPIC, topic);
		ComponentProperties componentProperties = new ComponentProperties(config);
		return (LdiOutput) autoConfig.ldiKafkaOutConfigurator().configure(componentProperties);
	}

	private Model createModelWithStatement() {
		Model model = ModelFactory.createDefaultModel();
		model.add(createResource("http://data-from-source/"), createProperty("http://test/"), "Data!");
		return model;
	}
}
