package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.springframework.kafka.core.KafkaTemplate;

public class LdioKafkaOut implements LdiOutput {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Lang outputLang;
    private final String topicName;

    public LdioKafkaOut(KafkaTemplate<String, String> kafkaTemplate, Lang outputLang, String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.outputLang = outputLang;
        this.topicName = topicName;
    }

    @Override
    public void accept(Model model) {
        this.kafkaTemplate.send(topicName, RDFWriter.source(model).lang(outputLang).build().asString());
    }

}
