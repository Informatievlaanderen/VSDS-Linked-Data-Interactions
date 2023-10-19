package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdfFormatter.RdfFormatter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

@SuppressWarnings("java:S2629")
public class LdiConsoleOut implements LdiOutput {
	private final Logger log = LoggerFactory.getLogger(LdiConsoleOut.class);

	private final Lang outputLanguage;
	private final String frameType;

	public LdiConsoleOut(Lang outputLanguage, String frameType) {
		this.outputLanguage = outputLanguage;
		this.frameType = frameType;
	}

	@Override
	public void accept(Model model) {
		log.info(RdfFormatter.formatModel(model, outputLanguage, frameType));
	}

	public static Lang getLang(MediaType contentType) {
		return ofNullable(nameToLang(contentType.getType() + "/" + contentType.getSubtype()))
				.orElseGet(() -> ofNullable(nameToLang(contentType.getSubtype()))
						.orElseThrow());
	}
}
