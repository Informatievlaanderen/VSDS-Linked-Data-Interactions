package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LdioKafkaInAutoConfigTest {

    @Test
    void shouldThrowExceptionWhenInvalidAuthConfig() {
        var configurator = new LdioKafkaInAutoConfig.LdioKafkaInConfigurator();

        Map<String, String> config = getBasicConfig();
        config.put(KafkaInConfigKeys.SECURITY_PROTOCOL, "Fantasy protocol");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                configurator.configure(null, null, new ComponentProperties(config)));

        assertEquals("Invalid 'security-protocol', the supported protocols are: [NO_AUTH, SASL_SSL_PLAIN]",
                exception.getMessage());
    }

    @Test
    void shouldNotThrowExceptionWhenNoAuthConfig() {
        var configurator = new LdioKafkaInAutoConfig.LdioKafkaInConfigurator();

        Map<String, String> config = getBasicConfig();

        assertDoesNotThrow(() -> configurator.configure(null, null, new ComponentProperties(config)));
    }

    private Map<String, String> getBasicConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(KafkaInConfigKeys.BOOTSTRAP_SERVERS, "servers");
        config.put(KafkaInConfigKeys.TOPICS, "topic1");
        config.put("orchestrator.name", "orchestrator.name");
        config.put("pipeline.name", "pipeline.name");
        return config;
    }

}