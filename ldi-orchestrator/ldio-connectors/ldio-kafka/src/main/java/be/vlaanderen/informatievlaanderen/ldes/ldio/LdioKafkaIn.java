package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.auth.KafkaAuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.auth.SaslSslPlainConfigProvider;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.SecurityProtocolNotSupportedException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.listener.LdioKafkaInListener;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.SASL_JAAS_PASSWORD;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig.ORCHESTRATOR_NAME;

public class LdioKafkaIn extends LdioInput {
    public static final String NAME = "Ldio:KafkaIn";
    @SuppressWarnings("java:S3740")
    private final KafkaMessageListenerContainer container;
    private final LdioKafkaInListener listener;
    /**
     * Creates a LdiInput with its Component Executor and LDI Adapter
     *
     * @param pipelineName
     * @param executor                  Instance of the Component Executor. Allows the LDI Input to pass
     *                                  data on the pipeline
     * @param adapter                   Instance of the LDI Adapter. Facilitates transforming the input
     *                                  data to a linked data model (RDF).
     * @param observationRegistry
     * @param applicationEventPublisher
     * @param config
     */
    public LdioKafkaIn(String pipelineName, ComponentExecutor executor, LdiAdapter adapter, ObservationRegistry observationRegistry,
                       ApplicationEventPublisher applicationEventPublisher, ComponentProperties config) {
        super(NAME, pipelineName, executor, adapter, observationRegistry, applicationEventPublisher);
        this.listener = new LdioKafkaInListener(getContentType(config), this::processInput);
        var consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerConfig(config));
        ContainerProperties containerProps = new ContainerProperties(config.getProperty(TOPICS).split(","));
        containerProps.setMessageListener(listener);
        this.container = new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
        container.start();
        this.starting();
    }

    @Override
    protected void resume() {
        container.resume();
    }

    @Override
    protected void pause() {
        container.pause();
    }

    private String getContentType(ComponentProperties config) {
        return config
                .getOptionalProperty(CONTENT_TYPE)
                .map(RDFLanguages::contentTypeToLang)
                .map(Lang::getHeaderString)
                .orElse(Lang.NQUADS.getHeaderString());
    }

	@Override
	public void shutdown() {
		container.pause();
	}
    private Map<String, Object> getConsumerConfig(ComponentProperties config) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getProperty(BOOTSTRAP_SERVERS));
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                config.getOptionalProperty(GROUP_ID).orElse(defineUniqueGroupName(config)));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                config.getOptionalProperty(AUTO_OFFSET_RESET).orElse("earliest"));
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        var authStrategy = KafkaAuthStrategy.from(config.getOptionalProperty(SECURITY_PROTOCOL)
                        .orElse(KafkaAuthStrategy.NO_AUTH.name()))
                .orElseThrow(() -> new SecurityProtocolNotSupportedException(SECURITY_PROTOCOL));

        if (KafkaAuthStrategy.SASL_SSL_PLAIN.equals(authStrategy)) {
            final String user = config.getProperty(SASL_JAAS_USER);
            final String password = config.getProperty(SASL_JAAS_PASSWORD);
            props.putAll(new SaslSslPlainConfigProvider().createSaslSslPlainConfig(user, password));
        }

        return props;
    }

    private String defineUniqueGroupName(ComponentProperties config) {
        return String.format("ldio-%s-%s", config.getProperty(ORCHESTRATOR_NAME),
                config.getPipelineName());
    }
}
