package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.StringWriter;

import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

public class LdioKafkaOut implements LdiOutput {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public LdioKafkaOut(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void accept(Model model) {
        this.kafkaTemplate.send("quickstart-events", RDFWriter.source(model).lang(Lang.NQUADS).build().asString());
    }


    public static String toString(final Model model, final Lang lang) {
        StringWriter stringWriter = new StringWriter();
        RDFDataMgr.write(stringWriter, model, lang);
        return stringWriter.toString();
    }

}
