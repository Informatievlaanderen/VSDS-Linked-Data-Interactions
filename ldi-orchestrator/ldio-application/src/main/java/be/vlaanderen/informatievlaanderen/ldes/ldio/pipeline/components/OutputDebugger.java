package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around any LdiOutput for debugging purposes
 */
public class OutputDebugger implements LdiOutput {
	private final Logger log;
	private final LdiOutput ldiOutput;

	public OutputDebugger(LdiOutput ldiOutput) {
		this.ldiOutput = ldiOutput;
		log = LoggerFactory.getLogger(ldiOutput.getClass());
	}

	@Override
	public void accept(Model model) {
		log.atDebug().log("Starting model: \n" + RDFWriter.source(model).lang(Lang.TTL).asString());
		ldiOutput.accept(model);
	}
}
