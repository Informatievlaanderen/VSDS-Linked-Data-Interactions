package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioAmqpOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.LdioOutputConfigurator;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties.RDF_WRITER;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.AmqpConfig.*;

@Configuration
public class LdioAmqpOutAutoConfig {

    @SuppressWarnings("java:S6830")
    @Bean(LdioAmqpOut.NAME)
    public LdioJmsOutConfigurator ldioConfigurator() {
        return new LdioJmsOutConfigurator();
    }

    public static class LdioJmsOutConfigurator implements LdioOutputConfigurator {

        @Override
        public LdiComponent configure(ComponentProperties config) {
            final var pipelineName = config.getPipelineName();
            final var remoteUrl = new RemoteUrlExtractor(config).getRemoteUrl();
            final var connectionFactory =
                    new JmsConnectionFactory(config.getProperty(USERNAME), config.getProperty(PASSWORD), remoteUrl);
            final var jmsTemplate = new JmsTemplate(connectionFactory);
            final var rdfWriter = LdiRdfWriter.getRdfWriter(
                    new LdiRdfWriterProperties(config.extractNestedProperties(RDF_WRITER).getConfig())
            );
            return new LdioAmqpOut(config.getProperty(QUEUE), jmsTemplate, pipelineName, rdfWriter);
        }

    }
}
