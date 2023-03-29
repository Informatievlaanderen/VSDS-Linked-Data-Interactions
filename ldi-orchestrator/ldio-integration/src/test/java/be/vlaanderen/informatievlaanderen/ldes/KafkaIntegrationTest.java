package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioKafkaInAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioKafkaOutAutoConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;
import static org.apache.jena.rdf.model.ResourceFactory.createStringLiteral;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EmbeddedKafka(brokerProperties = { "listeners=PLAINTEXT://localhost:9095", "port=9095" })
public class KafkaIntegrationTest {


    final LdioKafkaOutAutoConfig autoConfig = new LdioKafkaOutAutoConfig();

    private String topic = "embedded-test-topic";

    @Test
    public void givenEmbeddedKafkaBroker_whenSendingWithSimpleProducer_thenMessageReceived()
            throws Exception {

//        content-type: text/turtle
//        bootstrap-server: localhost:9092
//        topic-name: quickstart-events-2

        createConsumer("localhost:9095");

        Map<String, String> config = new HashMap<>();
        config.put("content-type", "text/turtle");
        config.put("bootstrap-server", "localhost:9095");
        config.put("topic-name", topic);
        ComponentProperties componentProperties = new ComponentProperties(config);
        LdiOutput output = (LdiOutput) autoConfig.ldiKafkaOutConfigurator().configure(componentProperties);


        Model model = ModelFactory.createDefaultModel();
        model.add(createResource("http://data-from-source/"), createProperty("http://test/"), "Data!");

        output.accept(model);

        Thread.sleep(1000);

        assertEquals(1, result.size());
        assertTrue(model.isIsomorphicWith(result.get(0)));
    }

    List<Model> result = new ArrayList<>();

    private void createConsumer(String s) {
        final LdioKafkaInAutoConfig autoConfig = new LdioKafkaInAutoConfig();
        ComponentExecutor componentExecutor = new ComponentExecutor(){
            @Override
            public void transformLinkedData(Model linkedDataModel) {
                result.add(linkedDataModel);
            }
        };
        Map<String, String> config = new HashMap<>();
        config.put("content-type", "text/turtle");
        config.put("bootstrap.servers", "localhost:9095");
        config.put("topic", topic);
        ComponentProperties componentProperties = new ComponentProperties(config);


        @SuppressWarnings("unchecked") var container = (KafkaMessageListenerContainer<String, String>)
                new LdioKafkaInAutoConfig().ldioConfigurator().configure(new RdfAdapter(), componentExecutor, componentProperties);

        System.out.println(container.isContainerPaused());
        container.start();
        System.out.println(container.isContainerPaused());
    }

}
