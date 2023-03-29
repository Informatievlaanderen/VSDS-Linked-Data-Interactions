package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.LangBuilder;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class LdioKafkaOutAutoConfig {

    private static final Lang DEFAULT_OUTPUT_LANG = Lang.NQUADS;

    @Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut")
    public LdioConfigurator ldiHttpOutConfigurator() {
        return new LdioConfigurator() {
            @Override
            public LdiComponent configure(ComponentProperties config) {
                config.getOptionalProperty("content-type").orElse(DEFAULT_OUTPUT_LANG)
                LangBuilder.create().contentType("bla").build();
                return new LdioKafkaOut(createKafkaTemplate("localhost:9092"), outputLang);
            }
        };
    }

    private KafkaTemplate<String, String> createKafkaTemplate(String bootstrapServer) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }

}
