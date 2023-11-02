package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioProcessor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorDebugger extends LdioProcessor {
    private final Logger log;

    public ProcessorDebugger(LdioProcessor ldiTransformer) {
        log = LoggerFactory.getLogger(ldiTransformer.getClass());
    }

    @Override
    public void apply(Model model) {
        log.debug("Starting model: \n" + RDFWriter.source(model).lang(Lang.TTL).asString());
        this.next(model);
    }
}
