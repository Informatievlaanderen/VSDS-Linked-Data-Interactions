package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriter.getRdfWriter;

@SuppressWarnings("java:S2629")
public class LdiConsoleOut implements LdiOutput {
	public static final String NAME = "Ldio:ConsoleOut";
	private final Logger log = LoggerFactory.getLogger(LdiConsoleOut.class);
	private final LdiRdfWriter ldiRdfWriter;

	public LdiConsoleOut(LdiRdfWriterProperties properties) {
		ldiRdfWriter = getRdfWriter(properties);
	}

	@Override
	public void accept(Model model) {
		log.info(ldiRdfWriter.write(model));
	}
}
