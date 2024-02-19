package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.AmqpConfig.CONTENT_TYPE_HEADER;

public class LdioAmqpOut implements LdiOutput {

    private static final Logger log = LoggerFactory.getLogger(LdioAmqpOut.class);

    public static final String NAME = "Ldio:AmqpOut";
    private final String queue;
    private final JmsTemplate jmsTemplate;
    private final String pipelineName;

    private final LdiRdfWriter rdfWriter;

    public LdioAmqpOut(String queue, JmsTemplate jmsTemplate, String pipelineName, LdiRdfWriter rdfWriter) {
        this.queue = queue;
        this.jmsTemplate = jmsTemplate;
        this.pipelineName = pipelineName;
        this.rdfWriter = rdfWriter;
    }

    @Override
    public void accept(Model model) {
        final var message = rdfWriter.write(model);
        logOutgoingRequest(message);
        jmsTemplate.convertAndSend(queue, message, msg -> {
            msg.setStringProperty(CONTENT_TYPE_HEADER, rdfWriter.getContentType());
            return msg;
        });
    }

    private void logOutgoingRequest(String message) {
        log.atDebug().log("Pipeline[{}]/Queue[{}]: Writing message to queue: {}", pipelineName, queue, message);
    }
}
