package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import io.cucumber.java.en.Given;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

public class KafkaCommonIntegrationSteps extends KafkaIntegrationTest  {
	public static String topic;
	public static final String bootstrapServer = "localhost:9095";
	private static final EmbeddedKafkaBroker embeddedKafkaBroker;
	static {
		embeddedKafkaBroker = new EmbeddedKafkaBroker(1, true, 1);
		embeddedKafkaBroker
				.brokerProperty("listeners", "PLAINTEXT://%s".formatted(bootstrapServer))
				.brokerProperty("port", 9095);
		embeddedKafkaBroker.afterPropertiesSet();
	}

//	@After
//	public void cleanup() {
//		embeddedKafkaBroker.doWithAdmin(adminClient -> {
//            try {
//                adminClient.deleteTopics(List.of("nquads", "rdfxml")).all().wait();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        });
//	}

	@Given("^I create a topic for my scenario: (.*)$")
	public void iCreateATopic(String topic) {
		this.topic = topic;
		embeddedKafkaBroker.addTopics(topic);
	}

}
