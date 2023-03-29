package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
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
        return config -> {
            final Lang lang = getLang(config);
            final String topicName = config.getProperty("topic-name");
            final String bootstrapServer = config.getProperty("bootstrap-server");
            return new LdioKafkaOut(createKafkaTemplate(bootstrapServer), lang, topicName);
        };
    }

    private static Lang getLang(ComponentProperties config) {
        return config
                .getOptionalProperty("content-type")
                .map(RDFLanguages::contentTypeToLang)
                .orElse(DEFAULT_OUTPUT_LANG);
    }

    private KafkaTemplate<String, String> createKafkaTemplate(String bootstrapServer) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }

}
