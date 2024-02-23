package be.vlaanderen.informatievlaanderen.ldes.ldio.components;

import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformerDebugger extends LdioTransformer {
	private final Logger log;

	public TransformerDebugger(LdioTransformer ldiTransformer) {
		log = LoggerFactory.getLogger(ldiTransformer.getClass());
	}

	@Override
	public void apply(Model model) {
		log.atDebug().log("Starting model: \n {}", RDFWriter.source(model).lang(Lang.TTL).asString());
		this.next(model);
	}
}
