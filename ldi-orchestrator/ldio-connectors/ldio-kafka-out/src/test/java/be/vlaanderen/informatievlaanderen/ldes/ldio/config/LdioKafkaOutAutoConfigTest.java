package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;
import static org.apache.jena.rdf.model.ResourceFactory.createStringLiteral;
import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@EmbeddedKafka(brokerProperties = { "listeners=PLAINTEXT://localhost:9095", "port=9095" })
class LdioKafkaOutAutoConfigTest {

    // TODO: 29/03/2023 remove, replaced by IT

    final LdioKafkaOutAutoConfig autoConfig = new LdioKafkaOutAutoConfig();


//    @Value("${test.topic}")
    private String topic = "embedded-test-topic";

    @Test
    public void givenEmbeddedKafkaBroker_whenSendingWithSimpleProducer_thenMessageReceived()
            throws Exception {

//        content-type: text/turtle
//        bootstrap-server: localhost:9092
//        topic-name: quickstart-events-2

        createKafkaTemplate("localhost:9095");

        Map<String, String> config = new HashMap<>();
        config.put("content-type", "text/turtle");
        config.put("bootstrap-server", "localhost:9095");
        config.put("topic-name", "quickstart-events");
        ComponentProperties componentProperties = new ComponentProperties(config);
        LdiOutput output = (LdiOutput) autoConfig.ldiKafkaOutConfigurator().configure(componentProperties);


        Model defaultModel = ModelFactory.createDefaultModel();
        defaultModel.add(createStatement(createResource("uri"), createProperty("uri"), createStringLiteral("lite")));

        output.accept(defaultModel);

        Thread.sleep(1000);

        System.out.println(result);
    }

    private void createKafkaTemplate(String bootstrapServer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(props);

//        consumer
        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setMessageListener((MessageListener<String, String>) data -> result.add("hi"));
        KafkaMessageListenerContainer container = new KafkaMessageListenerContainer(consumerFactory, containerProps);
        container.start();
    }

    List<String> result = new ArrayList<>();

}