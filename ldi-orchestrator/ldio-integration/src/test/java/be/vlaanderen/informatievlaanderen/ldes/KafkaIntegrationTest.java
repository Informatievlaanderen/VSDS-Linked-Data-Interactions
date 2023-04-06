package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioKafkaInAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioKafkaOutAutoConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.riot.RDFLanguages.nameToLang;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@EmbeddedKafka(brokerProperties = { "listeners=PLAINTEXT://localhost:9095", "port=9095" })
class KafkaIntegrationTest {

	final LdioKafkaOutAutoConfig autoConfig = new LdioKafkaOutAutoConfig();

	private static final String topic = "embedded-test-topic";
	private final String bootstrapServer = "localhost:9095";
	private final String kafkaOutContentType = "text/turtle";

	private List<LdiAdapter.Content> result;

	private static EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1, true, 1);

	@BeforeAll
	static void foo() {
//		embeddedKafkaBroker.kafkaPorts(9095);
		embeddedKafkaBroker
				.brokerProperty("listeners", "PLAINTEXT://%s".formatted("localhost:9095"))
				.brokerProperty("port", 9095);
//				.brokerProperty("security.inter.broker.protocol", "PLAINTEXT")
//				.brokerProperty("sasl.enabled.mechanisms", "PLAIN")
//				.brokerProperty("sasl.mechanism.inter.broker.protocol", "PLAIN");
//		embeddedKafkaBroker.afterPropertiesSet();
		embeddedKafkaBroker.afterPropertiesSet();
		embeddedKafkaBroker.addTopics(topic);


	}

	@BeforeEach
	void setUp() {
		result = new ArrayList<>();
	}

	@Test
	void givenEmbeddedKafkaBroker_whenSendingWithSimpleProducer_thenMessageReceived() {
		var kafkaListener = createKafkaListener();
		kafkaListener.start();

		var model = createModelWithStatement();

		var output = createKafkaOutput();
		output.accept(model);

		await().until(() -> result.size() == 1);
		assertTrue(model.isIsomorphicWith(toModel(result.get(0))));
		assertEquals(kafkaOutContentType, result.get(0).mimeType());
	}

	private Model createModelWithStatement() {
		Model model = ModelFactory.createDefaultModel();
		model.add(createResource("http://data-from-source/"), createProperty("http://test/"), "Data!");
		return model;
	}

	private LdiOutput createKafkaOutput() {
		final Map<String, String> config = new HashMap<>();
		config.put(KafkaOutConfigKeys.CONTENT_TYPE, kafkaOutContentType);
		config.put(KafkaOutConfigKeys.BOOTSTRAP_SERVERS, bootstrapServer);
		config.put(KafkaOutConfigKeys.TOPIC, topic);
		ComponentProperties componentProperties = new ComponentProperties(config);
		return (LdiOutput) autoConfig.ldiKafkaOutConfigurator().configure(componentProperties);
	}

	@SuppressWarnings("unchecked")
	private KafkaMessageListenerContainer<String, String> createKafkaListener() {
		final Map<String, String> config = new HashMap<>();
		config.put("bootstrap-servers", bootstrapServer);
		config.put("topics", topic);
		config.put("orchestrator.name", "orchestratorName");
		config.put("pipeline.name", "pipelineName");
		ComponentProperties componentProperties = new ComponentProperties(config);
		final ComponentExecutor componentExecutor = linkedDataModel -> {
		};
		final LdiAdapter adapter = content -> {
			result.add(content);
			return Stream.of(toModel(content));
		};
		return (KafkaMessageListenerContainer<String, String>) new LdioKafkaInAutoConfig().ldioConfigurator()
				.configure(adapter, componentExecutor, componentProperties);
	}

	private Model toModel(LdiAdapter.Content content) {
		return RDFParserBuilder.create()
				.fromString(content.content()).lang(nameToLang(content.mimeType())).toModel();
	}

}
