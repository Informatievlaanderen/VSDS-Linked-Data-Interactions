package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

@SuppressWarnings("java:S2629")
public class LdiConsoleOut implements LdiOutput {
	private final Logger log = LoggerFactory.getLogger(LdiConsoleOut.class);

	private final Lang outputLanguage;

	public LdiConsoleOut(Lang outputLanguage) {
		this.outputLanguage = outputLanguage;
	}

	int count = 0;

	@Override
	public void accept(Model model) {
		// log.info(RDFWriter.source(model)
		// .lang(outputLanguage)
		// .asString());
		log.info(String.valueOf(++count));
	}

	public static Lang getLang(MediaType contentType) {
		return ofNullable(nameToLang(contentType.getType() + "/" + contentType.getSubtype()))
				.orElseGet(() -> ofNullable(nameToLang(contentType.getSubtype()))
						.orElseThrow());
	}
}
