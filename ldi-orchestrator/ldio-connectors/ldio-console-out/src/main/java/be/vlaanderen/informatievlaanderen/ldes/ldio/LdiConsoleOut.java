package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.StringWriter;

import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.Lang.TURTLE;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

@SuppressWarnings("java:S2629")
public class LdiConsoleOut implements LdiOutput {
	private final Logger LOGGER = LoggerFactory.getLogger(LdiConsoleOut.class);

	private final Lang outputLanguage;

	public LdiConsoleOut(Lang outputLanguage) {
		this.outputLanguage = outputLanguage;
	}

	@Override
	public void accept(Model model) {
		LOGGER.info(RDFWriter.source(model)
				.lang(outputLanguage)
				.asString());
	}

	public static Lang getLang(MediaType contentType) {
		return ofNullable(nameToLang(contentType.getType() + "/" + contentType.getSubtype()))
				.orElseGet(() -> ofNullable(nameToLang(contentType.getSubtype()))
						.orElseThrow());
	}
}
